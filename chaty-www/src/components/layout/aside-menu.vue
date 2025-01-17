<template>
  <div class="menu-bar">
    <el-menu :default-active="activeIndex" class="el-menu-demo" mode="vertical" :ellipsis="false" :collapse="isCollapse"
      router active-text-color="#3981FF">
      <el-menu-item v-for="item in menuItems" v-permission="item.authority" v-bind="item" :key="item.index">
        <component class="icons" :is="item.icon"></component>
        <template #title>{{ $t(item.t) }}</template>
      </el-menu-item>
    </el-menu>

    <el-image src="/icon/collapse.png" class="collapse-icon" @click="isCollapse = !isCollapse"></el-image>
  </div>
</template>

<script>
export default {
  props: {
    menuItems: {
      type: Array,
      default: () => []
    }
  },
  data() {
    return {
      activeIndex: '/review',
      isCollapse: false
    }
  },
  created() {
    this.activeIndex = this.$route.path
  },
  mounted() {
    this.isCollapse = false
  },
  watch: {
    $route(to) {
      const pathSegments = to.path.split('/'); // 根据 '/' 分隔路径
      this.activeIndex = pathSegments[1] || '';
      if((pathSegments[1] || '' ) === 'docconfig') {
        this.activeIndex = '/correctConfigPackages'
      }
    },
    isCollapse(val) {
      this.$emit('collapse', val)
    }
  },
  methods: {}
}
</script>

<style lang="scss" scoped>
.menu-bar {
  display: flex;
  flex-direction: column;
  border-right: thin solid #dcdfe6;
  width: 176px;
  height: 100%;
  opacity: 0.82;
  padding: 10px 8px;
  z-index: 1;
  background-image: linear-gradient(180deg, #FFFFFF 0%, #ffffffd1 100%);

  .el-menu-item {
    width: 160px;
    height: 42px;
    font-weight: 400;
    font-size: 14px;
    color: #333333;
  }

  .el-menu-item:focus {
    font-weight: 400;
    font-size: 14px;
    color: #3981FF;
    background: #1677ff1a;
    border-radius: 4px;
  }

  .el-menu-item.is-active {
    font-weight: bolder;
    font-size: 14px;
    color: #3981FF;
    background: #1677ff1a;
    border-radius: 4px;
  }

  .el-menu-item:hover {
    font-weight: 400;
    font-size: 14px;
    color: #3981FF;
    background: #1677ff1a;
    border-radius: 4px;
  }

  .el-menu-demo {
    margin-top: 10px;
    border-right: none !important;
  }

  .flex-grow {
    flex-grow: 1;
  }

  .el-menu-demo {
    display: flex;
    flex-direction: column;
    border-right: none !important;
  }

  .menu-bottom {
    .icons {
      width: 15px;
      height: 15px;
    }
  }

  .icons {
    width: 15px;
    height: 15px;
    margin-right: 10px;
  }
  .collapse-icon {
    width: 20px;
    height: 20px;
    bottom: 20px;
    left: 141px;
    position: fixed;
  }
}

</style>