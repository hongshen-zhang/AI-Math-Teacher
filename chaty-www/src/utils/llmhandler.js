export function parseLLMResp(text, keys) {
	const result = {};
	keys.forEach(key => {
		result[key] = "";
	});

	const lines = text.split("\n");
	let currentSection = "";

	const regExp = new RegExp(`^\s*(${keys.join("|")})[:：]`);

	for (const line of lines) {
		let match = line.match(regExp);
		if (match) {
			currentSection = match[1];
			result[currentSection] += line.replace(match[0], "").trim() + " \n";
		} else {
			if (currentSection) {
				result[currentSection] += line.trim() + " \n";
			}
		}
	}

	keys.forEach(key => {
		result[key] = result[key].trim();
	});

	return result;
}

export function replacePromot(text, ctx) {
	return text.replace(/\${(\w+)}/g, function (match, key) {
		return ctx[key] || match;
	});
}