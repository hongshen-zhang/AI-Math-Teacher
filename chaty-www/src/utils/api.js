export function debounce(func, wait = 1000) {
    let timeout;

    return function (...args) {
        const context = this;

        clearTimeout(timeout);
        timeout = setTimeout(() => func.apply(context, args), wait);
    };
}