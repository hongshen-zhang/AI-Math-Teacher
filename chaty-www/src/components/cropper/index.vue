<template>
  <div class="cropper-wrapper" ref="cropperWrapper">
    <div v-if="imgUrl && !startClick" class="cropper-container" >
      <img class="cropper-img" :src="imgUrl" id="img" crossorigin="anonymous">
    </div>
    <div v-else-if="imgUrl && startClick" class="cropper-container" >
      <canvas  ref="canvas"  @click="recordClick"></canvas>
    </div>
    <template v-else>
      <slot name="empty">
        <el-empty class="empty-tip" description="加载中..." />
      </slot>
    </template>
  </div>
</template>

<script>
import Cropper from 'cropperjs'
import { ElLoading } from 'element-plus';
export function img2base64(imgUrl) {
  return fetch(imgUrl)
      .then(res => res.blob())
      .then(blob => {
        return URL.createObjectURL(blob);
      })
}
export default {
  props: {
    loading:{
      type: Boolean,
      default: false
    }
  },
  watch: {
    loading: {
      immediate: true,
      handler(e) {
        if (e && this.$refs.cropperWrapper) {
          this.loadingInstance = ElLoading.service({
            lock: true,
            text: '加载中...',
            fullscreen: false,
            target: this.$refs.cropperWrapper,
            background: 'rgba(0, 0, 0, 0.7)'
          });
        } else {
          if (this.loadingInstance)
            this.loadingInstance.close();
        }
      }
    }
  },
  data() {
    return {
      imgUrl: null,
      cropper: null,
      clickedPoints: [], // 用于存储点击的坐标点
      startClick: false, // 控制是否可以记录点击点
      naturalHeight: null,
      naturalWidth: null,
      imageOffset: { x: 0, y: 0 }, // 图片在 canvas 中的偏移
      imageScale: 1, // 图片缩放比例
      selected: [],
      loadingInstance: null,
    }
  },
  methods: {
    setImg(url) {
      this.imgUrl = url;
      if (this.imgUrl) {
        this.refreshCropper();
      } else if (this.cropper) {
        this.cropper.destroy();
      }
    },
    refreshCropper(next) {
      if (this.cropper) {
        this.cropper.destroy();
      }
      this.$nextTick(() => {
        this.cropper = new Cropper(document.getElementById('img'), {
          autoCrop: false,
          dragMode: 'move',
          movable: true,
          zoomable: true,
          background: false,
          toggleDragModeOnDblclick: false,
          ready: () => {
            this.cropper.clear();
            this.cropper.setDragMode('move');
            if (next) next();
          },
        });
      });
    },
    reset() {
      if (this.cropper) {
        this.cropper.destroy();
      }
      this.imgUrl = null;
      this.cropper = null;
    },
    crop(area) {
      this.unCrop();
      this.cropper.setDragMode('crop');
      if (area) {
        this.cropper.crop();
        this.cropper.setData(area);
      }
    },
    unCrop() {
      this.cropper.clear();
      this.cropper.setDragMode('move');
    },
    doCrop() {
      let cropped = this.cropper.getData(true);
      this.unCrop();
      return cropped;
    },
    clearCropped() {
      this.cropper.clear();
    },
    // 记录点击坐标
    recordClick(event) {
      if (!this.startClick) return;

      const canvas = this.$refs.canvas;
      const rect = canvas.getBoundingClientRect();

      // 获取点击位置相对于 canvas 的坐标
      const clickX = event.clientX - rect.left;
      const clickY = event.clientY - rect.top;

      // 转换为图片的实际像素坐标
      const x = (clickX - this.imageOffset.x) / this.imageScale;
      const y = (clickY - this.imageOffset.y) / this.imageScale;

      // 检查点击是否在图片范围内
      if (x >= 0 && x <= this.image.naturalWidth && y >= 0 && y <= this.image.naturalHeight) {
        this.clickedPoints.push({ x: Math.round(x), y: Math.round(y) });
        console.log("点击位置的像素坐标:", { x: Math.round(x), y: Math.round(y) });
        let area = {
          x: Math.round(x),
          y: Math.round(y),
          width: 20,
          height: 20,
          rotate: 0,
          scaleX: 1,
          scaleY: 1
        }
        this.drawFlag(canvas, clickX, clickY);
        this.$emit('pointsUpdated', area, this.clickedPoints.length, this.selected); // 触发事件传递坐标点
      } else {
        console.log("点击位置超出图片范围");
      }
    },
    drawFlag(canvas, clickX, clickY) {
      const ctx = canvas.getContext('2d');
      ctx.drawImage(document.getElementById("flagimg"), clickX, clickY, 20, 20)
    },
    stopClickedPoints() {
      this.startClick = false; // 停止记录
    },
    // 清空点击的点并开始新记录
    clearPoints(areas, selected) {
      this.clickedPoints = []; // 清空记录的点击坐标点
      this.startClick = true; // 设置为 true 以允许重新开始记录
      this.selected = selected;
      this.setCanvasImg(this.imgUrl, areas); // 重新加载图片到 canvas
    },
    async setCanvasImg(url, areas) {
      this.imgUrl = url;
      this.image = new Image();
      this.image.crossOrigin = 'anonymous';
      this.image.src = await img2base64(url);
      this.image.onload = () => {
        this.drawImageToCanvas(areas); // 初次加载图片到 canvas
      };
    },
    drawRect(areas, scaleX, scaleY) {
      const canvas = this.$refs.canvas;
      const ctx = canvas.getContext('2d');
      const selected = this.selected;
      areas.forEach((rect, idx) => {
        if (!selected.includes(idx)) {
          return;
        }
        let { x, y, width, height } = rect.area
        ctx.strokeStyle = rect.color || 'black'; // 设置边框颜色
        ctx.lineWidth = 1; // 设置边框宽度
        const scaledX = x * this.imageScale + this.imageOffset.x;
        const scaledY = y * this.imageScale + this.imageOffset.y;
        const scaledWidth = width * this.imageScale;
        const scaledHeight = height * this.imageScale;
        ctx.strokeRect(scaledX, scaledY, scaledWidth, scaledHeight);
      })
    },
    drawImageToCanvas(areas) {
      const canvas = this.$refs.canvas;
      const ctx = canvas.getContext('2d');

      // 设置 canvas 尺寸为父容器大小
      const container = canvas.parentElement;
      canvas.width = container.clientWidth;
      canvas.height = container.clientHeight;

      // 计算缩放比例
      const scaleX = canvas.width / this.image.naturalWidth;
      const scaleY = canvas.height / this.image.naturalHeight;
      this.imageScale = Math.min(scaleX, scaleY); // 保持图片纵横比

      // 计算图片在 canvas 中的居中偏移
      this.imageOffset.x = (canvas.width - this.image.naturalWidth * this.imageScale) / 2;
      this.imageOffset.y = (canvas.height - this.image.naturalHeight * this.imageScale) / 2;

      // 清除画布并绘制图片
      ctx.clearRect(0, 0, canvas.width, canvas.height);
      ctx.drawImage(
          this.image,
          0, 0, this.image.naturalWidth, this.image.naturalHeight,
          this.imageOffset.x, this.imageOffset.y,
          this.image.naturalWidth * this.imageScale, this.image.naturalHeight * this.imageScale
      );
      this.drawRect(areas, scaleX, scaleY)
    },
  },
}
</script>

<style lang="scss" scoped>
.cropper-wrapper {
  padding: 20px;
  background-color: var(--el-bg-color-page);

  .cropper-container {
    width: 100%;
    height: 100%;

    .cropper-img {
      display: block;
      object-fit: contain;
      width: 100%;
      max-height: 100%;
    }
  }

  .empty-tip {
    height: 100%;
  }
}
</style>
