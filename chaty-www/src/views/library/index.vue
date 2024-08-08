<template>
    <div class="library-wrapper">
        <div class="side-bar">
            <el-menu :default-active="activeIndex" :ellipsis="false" router>
                <el-menu-item v-for="item in menuItems" v-bind="item" :key="item.index">{{ item.label }}</el-menu-item>
            </el-menu>
        </div>
        <div class="main-wrapper">
            <router-view v-slot="{ Component }">
                <keep-alive>
                    <component :is="Component" :key="$route.name" v-if="$route.meta.keepAlive" />
                </keep-alive>
                <component :is="Component" :key="$route.name" v-if="!$route.meta.keepAlive" />
            </router-view>
        </div>
    </div>
</template>

<script>
export default {
    data() {
        return {
            activeIndex: '/library/question',
            menuItems: [
                { index: '/library/question', label: '题库' },
                { index: '/library/knowledge', label: '知识点' },
                { index: '/library/review', label: '批改作业' },
            ]
        }
    },
    created() {
        this.activeIndex = this.$route.path
    }
}
</script>

<style lang="scss" scoped>
.library-wrapper {
    display: flex;
    height: 100%;
    border: solid 1px var(--el-menu-border-color);
    
    .side-bar {
        width: 150px;
        height: 100%;
        padding-top: 20px;
        border-right: solid 1px var(--el-menu-border-color);

        :deep(.el-menu) {
            border-right: none;
        }
    }

    .main-wrapper {
        flex: 1;
        padding: 20px;
    }
}
</style>