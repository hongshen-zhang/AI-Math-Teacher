<template>
    <div class="menu-bar">
        <el-menu :default-active="activeIndex" class="el-menu-demo" mode="horizontal" :ellipsis="false" router>
            <el-menu-item index="0">Tool</el-menu-item>
            <div class="flex-grow" />
            <el-menu-item v-for="item in menuItems" v-bind="item" :key="item.index">{{ item.label }}</el-menu-item>
        </el-menu>
    </div>
</template>

<script>
export default {
    data() {
        return {
            activeIndex: '/',
            menuItems: [
                { index: '/', label: '作业批改' },
                // { index: '/imgocr', label: '图片转文字' },
                { index: '/solve', label: '一键解题' },
                { index: '/coursenote', label: '讲义' },
                { index: '/library/question', label: '知识库' },
                // { index: '/ai', label: 'AI' },
                // { index: '/config', label: '系统配置' },
            ]
        }
    },
    created() {
        this.activeIndex = this.$route.path
    }
}
</script>

<style lang="scss" scoped>
.flex-grow {
    flex-grow: 1;
}
</style>