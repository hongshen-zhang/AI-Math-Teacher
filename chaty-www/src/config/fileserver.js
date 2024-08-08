import config from "./index";

function fileurl(url) {
    return `${config.apiUrl}${url}` 
}

export default {
    uploadUrl: `${config.apiUrl}/api/file/upload`,
    fileurl,
}