<template>
    <div class="container-wrapper">
        <el-card class="left-bar">
            <template #header>
                <div class="card-header">
                    <span>选择图片</span>
                    <el-button class="button" type="primary" @click="submit">提交</el-button>
                </div>
            </template>
            <el-upload class="img-uploader" :show-file-list="false" :action="$fileserver.uploadUrl"
                :on-success="onImgUploadSuccess">
                <img v-if="imageUrl" :src="imageUrl" class="img-preview" />
                <el-icon v-else class="img-uploader-icon">
                    <Plus />
                </el-icon>
            </el-upload>
        </el-card>
        <el-card class="right-content">
            <template #header>
                <div class="card-header">
                    <span>解析结果</span>
                    <el-button class="button" type="primary" @click="clearContent">清空</el-button>
                </div>
            </template>
            <div class="result-wrapper">
                <el-text>
                    {{ content }}
                </el-text>
            </div>
        </el-card>
    </div>
</template>

<script>
export default {
    data() {
        return {
            imageUrl: '',
            content: '',
        }
    },
    methods: {
        onImgUploadSuccess(response) {
            this.imageUrl = this.$fileserver.fileurl(response.data.url)
        },
        submit() {
            if (!this.imageUrl) {
                this.$message.error('请先上传图片')
                return
            }
            // this.imageUrl = 'https://ocr-demo-1254418846.cos.ap-guangzhou.myqcloud.com/general/GeneralAccurateOCR/GeneralAccurateOCR2.jpg'
            this.$axios.post(`/api/ocr/url2text?url=${this.imageUrl}`).then(res => {
                this.$message.success("解析成功")
                this.content = res.data
            })
        },
        clearContent() {
            this.content = ''
        }
    }
}
</script>

<style lang="scss" scoped>
.container-wrapper {
    width: auto !important;
    display: flex;
    column-gap: 20px;

    .left-bar {
        width: 300px;

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .img-uploader {

            .img-preview {
                width: 260px;
                height: 178px;
                display: block;
            }

            .el-upload__tip {
                font-size: 12px;
                color: var(--el-text-color-regular);
                margin-top: 7px;
            }

            :deep(.el-upload) {
                border: 1px dashed var(--el-border-color);
                border-radius: 6px;
                cursor: pointer;
                position: relative;
                overflow: hidden;
                transition: var(--el-transition-duration-fast);
            }

            :deep(.el-upload:hover) {
                border-color: var(--el-color-primary);
            }

            :deep(.el-icon.img-uploader-icon) {
                font-size: 28px;
                color: #8c939d;
                width: 260px;
                height: 178px;
                text-align: center;
            }
        }
    }

    .right-content {
        flex: 1;

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .result-wrapper {
            height: calc(100vh - 220px);
        }
    }

}
</style>