<template>
    <div class="container-wrapper">
        <el-container>
            <el-header>
                <nav-bar></nav-bar>
            </el-header>
            <el-main class="container-main">
                <router-view v-slot="{ Component }">
                    <keep-alive>
                        <component :is="Component" :key="$route.name" v-if="$route.meta.keepAlive" />
                    </keep-alive>
                    <component :is="Component" :key="$route.name" v-if="!$route.meta.keepAlive" />
                </router-view>
            </el-main>
        </el-container>
    </div>
</template>//页面 相当于html

<script>
import NavBar from '../../components/layout/nav-bar.vue'
export default {
    components: {
        NavBar
    }
}
</script>//js逻辑

<style lang="scss" scoped>
.container-wrapper {
    width: 1280px;
    background: #fff;

    .container-main {
        height: calc(100vh - 60px);
    }
}
</style>//样式