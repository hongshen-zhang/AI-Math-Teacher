<template>
    <div class="page-footer">
        <el-row type="flex">
            <el-text style="color: white;">备案号：</el-text>
            <el-text class="footer-link" @click="toBeian">浙ICP备2024103593号</el-text>
        </el-row>
    </div>
</template>

<script>
export default {
    methods: {
        toBeian() {
            window.open('https://beian.miit.gov.cn/', '_blank');
        }
    }
}
</script>

<style lang="scss" scoped>
.page-footer {
    //border-top: solid 1px var(--el-menu-border-color);
    margin-top: 20px;
    padding: 20px 0;

    .footer-link {
        cursor: pointer;
        color: white;
    }
}
</style>