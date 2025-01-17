import {
	EventStreamContentType,
	fetchEventSource,
} from "@microsoft/fetch-event-source";
import config from "../config/index.js";
import { useUserStore } from "../store";

class RetriableError extends Error {}
class FatalError extends Error {}

export function postFetchEventSource(
	url,
	payload,
	onMessage,
	onFinish,
	onError
) {
	const ctrl = new AbortController();
	fetchEventSource(`${config.apiUrl || ''}${url}`, {
		method: "POST",
		headers: {
			"Content-Type": "application/json;charset=utf-8",
		},
		credentials: "include",
		body: JSON.stringify(payload),
		async onopen(response) {
			if (
				response.ok &&
				response.headers.get("content-type") === EventStreamContentType
			) {
				return; // everything's good
			} else if (
				response.status >= 400 &&
				response.status < 500 &&
				response.status !== 429
			) {
				if (response.status === 401) {
					let userStore = useUserStore();
					userStore.showLogin(true)
				}
				// client-side errors are usually non-retriable:
				throw new FatalError();
			} else {
				throw new RetriableError();
			}
		},
		onmessage: ev => {
			let data = JSON.parse(ev.data);
			onMessage(data);

			// 关闭
			if (data.$end) {
				if (onFinish) {
					onFinish();
				}
				ctrl.abort();
				return;
			}
		},
		onclose() {
			ctrl.abort();
		},
		onerror(err) {
			if (onError) {
				onError(err);
			}
			console.error(err);
			throw new FatalError();
		},
		signal: ctrl.signal,
	});

	return { 
		stop: () => ctrl.abort()
	 }
}
