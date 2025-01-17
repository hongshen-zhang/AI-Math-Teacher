// Description: 图片处理工具

export function drawRect(img, rects, answers, canvas, lineWidth = 5, strokeStyle = 'black') {
    const ctx = canvas.getContext('2d')

    // 设置canvas大小
    canvas.width = img.width;
    canvas.height = img.height;
    // 绘制图片
    ctx.drawImage(img, 0, 0);

    let rectWidth = 0 , rectHeight = 0;
    rects.forEach(rect => {
        if (!rect) {
            return
        }
        let { x, y, width, height } = rect
        // 绘制图片
        if (rect.mark) {
            rectWidth = rect.mark.width
            rectHeight = rect.mark.height
            ctx.drawImage(rect.mark, x + 70, y, 100 * rect.mark.width / rect.mark.height, 100)
            return
        }
        // 绘制方框
        ctx.strokeStyle = rect.color || strokeStyle; // 设置边框颜色
        ctx.lineWidth = lineWidth; // 设置边框宽度
        ctx.strokeRect(x, y, width, height); // 绘制方框
    })
    // 绘制答案
    if (answers && answers.length !== 0 ) {
        answers.forEach(answer => {
            if (answer.isEssay) {
                ctx.font = '90px Arial';
                ctx.fillStyle = 'red';
                let { x, y, width, height } = answer.area;
                ctx.fillText(answer.text, x + 100, y + 100);
            } else {
                ctx.font = '80px Arial';
                ctx.fillStyle = 'red';
                let { x, y, width, height } = answer.area;
                ctx.fillText(answer.text, x + rectWidth / 2 + 80, y + rectHeight / 2.5);
            }

        })
    }

    return canvas.toDataURL('image/jpeg');
}


export function img2base64(imgUrl) {
    return fetch(imgUrl)
        .then(res => res.blob())
        .then(blob => {
            return URL.createObjectURL(blob);
        })
}

export function extraImg(img, area) {
    const canvas = document.getElementById('correct-canvas');
    const ctx = canvas.getContext('2d');
    canvas.width = img.width;
    canvas.height = img.height;
    ctx.drawImage(img, 0, 0)
    const imageData = ctx.getImageData(area.x, area.y, area.width, area.height);
    
    const tempCanvas = document.createElement('canvas');
    tempCanvas.width = imageData.width;
    tempCanvas.height = imageData.height;
    const tempCtx = tempCanvas.getContext('2d');
    tempCtx.putImageData(imageData, 0, 0);

    // 获取 base64 格式的图像数据
    return tempCanvas.toDataURL('image/jpeg');
}

/**
 * 判断图片是否高度大于宽度
 */
export function isRotate(img) {
    return new Promise((resolve, reject) => {
        const image = new Image();
        image.onload = () => {
            console.log(image.width, image.height);
            resolve(image.width > image.height);
        };
        image.onerror = reject;
        image.src = img;
    });
}