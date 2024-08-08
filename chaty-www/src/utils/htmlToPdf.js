// 导出页面为PDF格式
import html2Canvas from "html2canvas";
import JsPDF from "jspdf";

export function getPdf(title, dom = "pdfDom") {
  // 当下载pdf时，若不在页面顶部会造成PDF样式不对,所以先回到页面顶部再下载
  let top = document.getElementById(dom);
  if (top != null) {
    top.scrollIntoView();
    top = null;
  }
  html2Canvas(document.querySelector(`#${dom}`), {
    allowTaint: true,
  }).then(function (canvas) {
    // 获取canvas画布的宽高
    let contentWidth = canvas.width;
    let contentHeight = canvas.height;
    // 一页pdf显示html页面生成的canvas高度;
    let pageHeight = (contentWidth / 595.28) * 841.89;
    // 未生成pdf的html页面高度
    let leftHeight = contentHeight;
    // 页面偏移
    let position = 0;
    // html页面生成的canvas在pdf中图片的宽高（本例为：横向a4纸[841.89,592.28]，纵向需调换尺寸）
    let imgWidth = 595.28;
    let imgHeight = (595.28 / contentWidth) * contentHeight;
    let pageData = canvas.toDataURL("image/jpeg", 1.0);
    let PDF = new JsPDF("p", "pt", "a4", true, 20);
    // 两个高度需要区分: 一个是html页面的实际高度，和生成pdf的页面高度
    // 当内容未超过pdf一页显示的范围，无需分页
    if (leftHeight < pageHeight) {
      PDF.addImage(pageData, "JPEG", 0, 0, imgWidth, imgHeight);
    } else {
      while (leftHeight > 0) {
        PDF.addImage(pageData, "JPEG", 0, position, imgWidth, imgHeight);
        leftHeight -= pageHeight;
        position -= 841.89;
        // 避免添加空白页
        if (leftHeight > 0) {
          PDF.addPage();
        }
      }
    }
    PDF.save(title + ".pdf");
  });
}
