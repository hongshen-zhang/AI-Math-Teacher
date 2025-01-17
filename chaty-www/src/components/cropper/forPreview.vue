<template>
  <div class="image-preview-wrapper" ref="imageWrapper">
    <canvas
        ref="canvas"
        class="preview-canvas"
        @mousedown="onMouseDown"
        @wheel="onWheelZoom"
        @contextmenu.prevent="onRightClick"
    ></canvas>
    <div
        v-if="!isEmpty && (!imgUrl || loading)"
        class="empty-tip"
        :class="{ loading: loading }"
    >
      <span class="loading-text">加载中...</span>
    </div>
    <div v-else-if="isEmpty"></div>

    <!-- 全屏时显示的按钮容器 -->
    <div v-if="isFullScreen" class="fullscreen-controls">
      <el-button @click="goToPreviousPage" class="fullscreen-btn" icon="ArrowLeft">
        上一页
      </el-button>
      <el-button @click="goToNextPage" class="fullscreen-btn" icon="ArrowRight">
        下一页
      </el-button>
    </div>

    <!-- 触发全屏模式的按钮 -->
    <el-button v-if="!isFullScreen" @click="requestFullScreen" class="fullscreen-button" icon="FullScreen">
    </el-button>
  </div>
</template>

<script>
import { useUserStore } from "@/store";

export default {
  props: {
    loading: {
      type: Boolean,
      default: false,
    },
    isEmpty: {
      type: Boolean,
      default: false,
    },
    id: {
      type: String,
      default: ''
    }
  },
  data() {
    return {
      imgUrl: null,
      scale: 1,
      translate: { x: 0, y: 0 },
      dragging: false,
      dragStart: { x: 0, y: 0 },
      image: null,
      canvasContext: null,
      lastMoveTime: 0,
      lastTime: 0,
      answers: null,
      rects: null,
      isFullScreen: false, // 控制是否全屏
    };
  },
  computed: {
    xyOffset() {
      const store = useUserStore();
      return store.getXYOffsetData(this.id);
    }
  },
  watch: {
    xyOffset: {
      handler(newVal, oldVal) {
        this.renderCanvas()
      },
      deep: true
    }
  },
  mounted() {
    const canvas = this.$refs.canvas;
    this.canvasContext = canvas.getContext("2d");
    this.setCanvasSize();

    window.addEventListener("mouseup", this.onMouseUp);
    window.addEventListener("resize", this.setCanvasSize);
    window.addEventListener("keydown", this.onKeyDown); // 监听键盘事件

    document.addEventListener('fullscreenchange', this.exitFullScreen);
  },
  beforeDestroy() {
    window.removeEventListener("mouseup", this.onMouseUp);
    window.removeEventListener("resize", this.setCanvasSize);
    window.removeEventListener("keydown", this.onKeyDown);
  },
  methods: {
    requestFullScreen() {
      const el = this.$refs.imageWrapper;
      if (!el) return;

      // 浏览器兼容处理
      if (el.requestFullscreen) {
        el.requestFullscreen();
      } else if (el.webkitRequestFullscreen) {
        el.webkitRequestFullscreen();
      } else if (el.mozRequestFullScreen) {
        el.mozRequestFullScreen();
      } else if (el.msRequestFullscreen) {
        el.msRequestFullscreen();
      } else {
        console.warn("当前浏览器不支持全屏 API");
      }
    },
    exitFullScreen() {
      this.isFullScreen = !this.isFullScreen;
    },
    goToPreviousPage() {
      this.$emit("arrow-key-pressed", -1);
    },
    goToNextPage() {
      this.$emit("arrow-key-pressed", 1);
    },

    onKeyDown(event) {
      if (event.key === "Escape") {
        this.exitFullScreen(); // 监听 ESC 键，退出全屏
      } else if (event.key === "ArrowLeft") {
        this.$emit("arrow-key-pressed", -1);
      } else if (event.key === "ArrowRight") {
        this.$emit("arrow-key-pressed", 1);
      } else if (event.key === "ArrowUp") {
        this.$emit("arrow-key-pressed", -1);
      } else if (event.key === "ArrowDown") {
        this.$emit("arrow-key-pressed", 1);
      }
    },

    setImg(url, rects, answers) {
      let needAdjust = this.imgUrl === null;
      this.imgUrl = url;
      this.rects = rects;
      this.answers = answers;
      const img = new Image();
      img.src = url;
      img.onload = () => {
        this.image = img;

        if (needAdjust) {
          this.adjustImageScale();
        }

        this.renderCanvas();
      };
      img.onerror = () => {
        console.error("图片加载失败");
      };
    },
    setCanvasSize() {
      const canvas = this.$refs.canvas;
      canvas.width = canvas.parentElement.offsetWidth;
      canvas.height = canvas.parentElement.offsetHeight;
      this.adjustImageScale();
      this.renderCanvas();
    },
    adjustImageScale() {
      if (!this.image) return;
      const canvas = this.$refs.canvas;
      const canvasWidth = canvas.width;
      const canvasHeight = canvas.height;

      const imgWidth = this.image.width;
      const imgHeight = this.image.height;

      const scaleX = canvasWidth / imgWidth;
      const scaleY = canvasHeight / imgHeight;

      this.scale = Math.min(scaleX, scaleY);

      this.translate.x = (canvasWidth - imgWidth * this.scale) / 2;
      this.translate.y = (canvasHeight - imgHeight * this.scale) / 2;
    },
    resetTransform() {
      this.scale = 1;
      this.translate = { x: 0, y: 0 };
      this.renderCanvas();
    },
    onMouseDown(event) {
      if (event.button !== 0) return;
      this.dragging = true;
      this.dragStart = { x: event.clientX, y: event.clientY };
      window.addEventListener("mousemove", this.onMouseMove);
    },
    onMouseMove(event) {
      if (!this.dragging) return;

      const now = Date.now();
      if (now - this.lastMoveTime < 1000 / 60) {
        return;
      }
      this.lastMoveTime = now;

      const dx = event.clientX - this.dragStart.x;
      const dy = event.clientY - this.dragStart.y;
      this.translate.x += dx;
      this.translate.y += dy;
      this.dragStart = { x: event.clientX, y: event.clientY };
      this.renderCanvas();
    },
    onMouseUp(event) {
      if (event.button === 0) {
        this.dragging = false;
        window.removeEventListener("mousemove", this.onMouseMove);
      }
    },
    onWheelZoom(event) {
      event.preventDefault();

      const now = Date.now();
      if (now - this.lastTime < 1000 / 60) {
        return;
      }
      this.lastTime = now;

      const canvas = this.$refs.canvas;
      const rect = canvas.getBoundingClientRect();

      const mouseX = event.clientX - rect.left;
      const mouseY = event.clientY - rect.top;

      const zoomFactor = event.deltaY > 0 ? 0.96 : 1.04;
      let newScale = this.scale * zoomFactor;
      newScale = Math.min(Math.max(newScale, 0.1), 10);

      const x = mouseX - this.translate.x;
      const y = mouseY - this.translate.y;

      this.translate.x -= (newScale - this.scale) * (x / this.scale);
      this.translate.y -= (newScale - this.scale) * (y / this.scale);

      this.scale = newScale;

      this.renderCanvas();
    },
    onRightClick(event) {
      const canvas = this.$refs.canvas;
      const rect = canvas.getBoundingClientRect();

      const mouseX = event.clientX - rect.left;
      const mouseY = event.clientY - rect.top;

      const imageX = (mouseX - this.translate.x) / this.scale - 50;
      const imageY = (mouseY - this.translate.y) / this.scale - 20;

      let clickedRect = null;
      let index = null;
      if (this.rects && Array.isArray(this.rects)) {
        let xyOffset = this.xyOffset;
        for (let rectItem of this.rects) {
          if (!rectItem) continue;
          const { x, y, width, height } = rectItem;
          if (
              imageX >= x + xyOffset.xOffset &&
              imageX <= x + 100 + xyOffset.xOffset &&
              imageY >= y + xyOffset.yOffset &&
              imageY <= y + 100 + xyOffset.yOffset
          ) {
            clickedRect = rectItem;
            index = this.rects.indexOf(rectItem);
            break;
          }
        }
      }

      if (clickedRect) {
        this.$emit("rect-right-clicked", index);
      }
    },
    renderCanvas() {
      const canvas = this.$refs.canvas;
      const ctx = this.canvasContext;

      if (!ctx || !this.image) return;

      ctx.clearRect(0, 0, canvas.width, canvas.height);

      ctx.save();
      ctx.translate(this.translate.x, this.translate.y);
      ctx.scale(this.scale, this.scale);

      ctx.drawImage(this.image, 0, 0);

      const strokeStyle = "blue";
      const lineWidth = 3 / this.scale;
      let xyOffset = this.xyOffset;
      if (this.rects && Array.isArray(this.rects)) {
        this.rects.forEach((rect) => {
          if (!rect) return;

          const { x: rectX, y: rectY, width, height } = rect;

          if (rect.mark) {
            const markWidth = (100 * rect.mark.width) / rect.mark.height;
            const markHeight = 100;
            ctx.drawImage(
                rect.mark,
                rectX + 70 + xyOffset.xOffset,
                rectY + xyOffset.yOffset,
                markWidth,
                markHeight
            );
            return;
          }

          ctx.strokeStyle = rect.color || strokeStyle;
          ctx.lineWidth = lineWidth;
          ctx.strokeRect(rectX, rectY, width, height);
        });
      }

      if (
          this.answers &&
          Array.isArray(this.answers) &&
          this.answers.length !== 0
      ) {
        this.answers.forEach((answer) => {
          const { x, y, width, height } = answer.area;

          ctx.font = answer.isEssay ? `80px Arial` : `70px Arial`;
          ctx.fillStyle = "red";
          ctx.textBaseline = "top";

          if (answer.isEssay) {
            ctx.fillText(answer.text, x + 100 + xyOffset.xOffset, y + 100 + xyOffset.yOffset);
          } else {
            ctx.fillText(answer.text, x + width / 2 + 160 + xyOffset.xOffset, y + height / 2.5 + xyOffset.yOffset);
          }
        });
      }

      ctx.restore();
    },
  },
};
</script>

<style lang="scss" scoped>
.image-preview-wrapper {
  width: 100%;
  height: 400px;
  overflow: hidden;
  background: #f5f5f5;
  position: relative;

  .preview-canvas {
    width: 100%;
    height: 100%;
    display: block;
    cursor: grab;
  }

  .preview-canvas:active {
    cursor: grabbing;
  }

  .empty-tip {
    position: absolute;
    top: 0;
    left: 0;
    width: 100%;
    height: 100%;
    display: flex;
    justify-content: center;
    align-items: center;
    background: rgba(0, 0, 0, 0.5);
    color: #fff;
    font-size: 18px;
    transition: opacity 0.3s;

    &.loading {
      font-size: 20px;
      color: #ffcc00;
    }

    .loading-text {
      animation: pulse 1.5s infinite;
    }
  }

  .fullscreen-button {
    position: absolute;
    bottom: 10px;
    right: 10px;
    z-index: 9;
    padding: 6px 12px;
    font-size: 14px;
    background-color: #007bff;
    color: #fff;
    border-radius: 4px;
    cursor: pointer;
    border: none;
  }

  .fullscreen-controls {
    position: absolute;
    bottom: 20px;
    left: 50%;
    transform: translateX(-50%);
    display: flex;
    gap: 10px;
    z-index: 10;
  }

  .fullscreen-btn {
    padding: 10px 20px;
    font-size: 16px;
    background-color: #007bff;
    color: white;
    border: none;
    border-radius: 5px;
    cursor: pointer;
  }

  @keyframes pulse {
    0%,
    100% {
      opacity: 1;
    }
    50% {
      opacity: 0.5;
    }
  }
}
</style>
