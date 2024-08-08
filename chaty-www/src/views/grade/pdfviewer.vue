<template>
    <div class="container-wrapper">
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <span>OCR识别结果</span>
                </div>
            </template>
            <div class="content-container">
                <el-text style="white-space: pre-wrap; word-break: break-all;">
                    {{ data.ocrText }}
                </el-text>
            </div>
        </el-card>
        <el-card class="box-card">
            <template #header>
                <div class="card-header">
                    <span>AI批改结果</span>
                </div>
            </template>
            <div class="content-container">
                <el-text style="white-space: pre-wrap; word-break: break-all;">
                    {{ data.aiText }}
                </el-text>
            </div>
        </el-card>
    </div>
</template>

<script>
export default {
    props: {
        data: {
            type: Object,
            default: () => { }
        }
    },
    methods: {
        // TODO
    }
}
</script>

<style lang="scss" scoped>
.container-wrapper {
    padding: 20px;
    display: flex;
    flex-direction: column;
    row-gap: 20px;

    .box-card {
        width: 100%;

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

    }
}
</style>