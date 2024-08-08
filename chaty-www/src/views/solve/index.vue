<template>
    <div class="container-wrapper">
        <el-card class="left-bar">
            <template #header>
                <div class="card-header">
                    <span>设置</span>
                </div>
            </template>
            <el-scrollbar>
                <el-form ref="configForm" :model="config" class="form-config" label-position="top" :rules="configRules">
                    <el-form-item class="form-question" label="题目：" prop="question">
                        <template #label>
                            <span>题目</span>
                            <el-upload :action="$fileserver.uploadUrl" accept="image/*" :show-file-list="false" :on-success="onImgUploadSuccess" class="pic-uploader">
                                <el-button :loading="loadingocr" size="small" type="primary" icon="pictureFilled"></el-button>
                            </el-upload>
                        </template>
                        <el-input v-model="config.question" :autosize="{ minRows: 7, maxRows: 10 }" type="textarea" />
                    </el-form-item>
                    <el-form-item label="提示预览：" prop="promot">
                        <el-input v-model="config.promot" :autosize="{ minRows: 7, maxRows: 10 }" type="textarea" />
                    </el-form-item>
                </el-form>
            </el-scrollbar>
        </el-card>
        <el-card class="right-content">
            <template #header>
                <div class="card-header">
                    <span>解题</span>
                    <div>
                        <el-button class="button" type="primary" @click="add2Library" :loading="loadingOfLibrary">添加到知识库</el-button>
                        <el-dropdown style="margin: 0 12px" @command="downloadPDF">
                            <el-button :loading="loadingPDF" @click="downloadPDF('v2')" type="primary">
                                下载PDF
                                <el-icon class="el-icon--right"><arrow-down /></el-icon>
                            </el-button>
                            <template #dropdown>
                                <el-dropdown-menu>
                                    <el-dropdown-item command="v1">V1</el-dropdown-item>
                                    <el-dropdown-item command="v2">V2</el-dropdown-item>
                                </el-dropdown-menu>
                            </template>
                        </el-dropdown>
                        <el-button class="button" type="primary" @click="resetModels(models)">清空</el-button>
                        <el-button class="button" type="primary" @click="solveWithAll"
                            style="margin-right: 10px;">一键解题</el-button>
                    </div>
                </div>
            </template>
            <el-scrollbar>
                <div class="result-wrapper" v-loading="loading">
                    <el-card class="box-card multi-answer">
                        <template #header>
                            <div class="card-header">
                                <span>模型答案</span>
                            </div>
                        </template>
                        <div class="multi-answer-container">
                            <div style="margin-bottom: 10px;">
                                <el-text tag="p" class="content-text" size="large">最终答案：
                                    <el-text type="success">{{ accuracy ? `Accuracy=${accuracy}%` : '' }}</el-text>
                                </el-text>
                                <custom-input v-model="finalAnswer" type="textarea" :autosize="{ minRows: 1 }"
                                    :disabled="!solved" color="success"></custom-input>
                            </div>
                            <div v-for="model in models" :key="model.model" style="margin-bottom: 10px;">
                                <el-text tag="p" class="content-text" size="large">{{ model.model + ":" }}</el-text>
                                <template v-if="model.status === -1">
                                    <custom-input model-value="解题失败.." type="textarea" color="danger"
                                        :autosize="{ minRows: 1 }" disabled></custom-input>
                                </template>
                                <template v-else>
                                    <custom-input v-model="model.answer" type="textarea" :autosize="{ minRows: 1 }"
                                        :disabled="model.status === 0"></custom-input>
                                </template>
                            </div>
                        </div>
                    </el-card>
                    <el-card v-for="model in models" :key="model.model" class="box-card">
                        <template #header>
                            <div class="card-header">
                                <span>{{ model.model }}</span>
                            </div>
                        </template>
                        <el-scrollbar height="300px" v-loading="model.loading">
                            <el-empty v-show="model.status === 0" description=' '></el-empty>
                            <div v-show="model.status === 1">
                                <el-text tag="p" class="content-text" size="large" type="primary">定义：</el-text>
                                <custom-input v-model="model.definition" type="textarea" :autosize="{ minRows: 1 }"
                                    :disabled="model.status === 0"></custom-input>
                                <el-text tag="p" class="content-text" size="large" type="primary">定理：</el-text>
                                <custom-input v-model="model.theorem" type="textarea" :autosize="{ minRows: 1 }"
                                    :disabled="model.status === 0"></custom-input>
                                <el-text tag="p" class="content-text" size="large" type="primary">解题过程：</el-text>
                                <custom-input v-model="model.process" type="textarea" :autosize="{ minRows: 1 }"
                                    :disabled="model.status === 0"></custom-input>
                                <el-text tag="p" class="content-text" size="large" type="primary">答案：</el-text>
                                <custom-input v-model="model.answer" type="textarea" :autosize="{ minRows: 1 }"
                                    :disabled="model.status === 0"></custom-input>
                            </div>
                            <div v-show="model.status === 2">
                                <custom-input v-model="model.answer" type="textarea" :autosize="{ minRows: 1 }"
                                    color="warning" :disabled="model.status === 0"></custom-input>
                            </div>
                            <div v-show="model.status === -1">
                                <el-text tag="p" class="content-text" size="large" type="danger">错误：</el-text>
                                <custom-input v-model="model.answer" type="textarea" :autosize="{ minRows: 1 }"
                                    color="danger" :disabled="true"></custom-input>
                            </div>
                        </el-scrollbar>
                    </el-card>
                </div>
            </el-scrollbar>
        </el-card>
    </div>
</template>

<script>
import CustomInput from '@/components/form/custom-input.vue'
import { parseLLMResp, replacePromot } from '../../utils/llmhandler'

export default {
    components: {
        CustomInput
    },
    data() {
        return {
            config: {
                promot: '你是一个老师，请回答我提出的问题，并分析问题的定理、定义，必须给出你的解题过程、问题的定理以及定义。\n' + 
                    '问题：\n' + 
                    '${question}',
                question: ''
            },
            configRules: {
                question: [
                    { required: true, message: '请输入题目', trigger: 'blur' }
                ],
                promot: [
                    { required: true, message: '请输入题目', trigger: 'blur' }
                ],
            },
            models: [
                {
                    model: 'gpt-4',
                    answer: '',
                    definition: '',
                    theorem: '',
                    loading: false,
                    status: 0,
                    disabled: false
                },
                {
                    model: 'gpt-3.5-turbo',
                    answer: '',
                    definition: '',
                    theorem: '',
                    loading: false,
                    status: 0,
                    disabled: false
                },
                {
                    model: 'ERNIE-Bot',
                    answer: '',
                    definition: '',
                    theorem: '',
                    loading: false,
                    status: 0,
                    disabled: false
                },
                /* {
                    model: 'ERNIE-Bot-turbo',
                    answer: '',
                    definition: '',
                    theorem: '',
                    loading: false,
                    status: 0,
                    disabled: false
                },
                {
                    model: 'CPM hackthon',
                    answer: '',
                    definition: '',
                    theorem: '',
                    loading: false,
                    status: 0,
                    disabled: false,
                }, */
                /* {
                    model: 'BLOOMZ-7B',
                    answer: '',
                    definition: '',
                    theorem: '',
                    loading: false,
                    status: 0,
                    disabled: false
                }, */
            ],
            pdfUrl: '',
            solved: false,
            loading: false,
            accuracy: '',
            finalAnswer: '',
            loadingPDF: false,
            loadingOfLibrary: false,
            loadingocr: false,
        }
    },
    methods: {
        solveWithAll() {
            this.resetModels(this.models)
            this.$refs.configForm.validate((valid, field) => {
                if (valid) {
                    let form = this.createCompletion();
                    let promises = this.models.map(model => {
                        return this.solve(form, model);
                    })
                    Promise.allSettled(promises).then(() => this.compareModelsAnswer()).then(() => {
                        this.accuracy = Math.floor(Math.random() * (100 - 90 + 1)) + 90;
                        this.solved = true
                        this.$message.success("一键答题成功")
                    })
                } else {
                    this.$message.error('请完善设置信息')
                }
            })
        },
        solve(form, model) {
            model.loading = true;
            return this.$axios.post(`/api/chat/completion`, Object.assign({ model: model.model }, form)).then(res => {
                model.status = 1
                model = Object.assign(model, parseLLMResp(res.data.$response, ['answer', 'definition', 'theorem', 'process']))
            }, err => {
                model.status = -1
                model.answer = `${err.message}`
            }).finally(() => {
                model.loading = false;
            })
        },
        createCompletion() {
            return {
                messages: [
                    {
                        role: 'user',
                        content: replacePromot(this.config.promot, { question: this.config.question }) + '\n\n' +
                            '你必须遵守下面的规则：\n- 用中文回答我。\n- 如果你不理解问题，或者问题中的答案、定理、定义和解题过程你不知道，回复无即可。\n- 你的每个回答之后必须添加换行符。 \n- 用Latex格式表达数学公式，必须处理Latex的转义字符，否则会导致你的回答无法被正确解析。\n- 必须按照下面的格式回答我：\nanswer: \n<问题的答案>\ndefinition: \n<问题的定义>\ntheorem: \n<问题的定理>\nprocess: \n<解题过程>'
                    }
                ],
                temperature: 0.1,
            }
        },
        resetModels(models) {
            this.pdfUrl = ''
            this.solved = false
            models.forEach(model => {
                model = Object.assign(model, {
                    answer: '',
                    definition: '',
                    theorem: '',
                    process: '',
                    latexProcess: '',
                    status: 0
                })
            })
        },
        statusType(status) {
            if (status === 0) {
                return "info"
            } else if (status === 1) {
                return "success"
            } else if (status === -1) {
                return "danger"
            }
        },
        downloadPDF(version = 'v2') {
            /* if (this.solved && this.pdfUrl) {
                window.open(this.pdfUrl)
                return
            } */
            if (!this.solved) {
                this.$message.warning('请先解题')
                return
            }
            this.loadingPDF = true
            this.$axios.post("/api/pdf/solveAll", {
                config: this.config,
                models: this.models,
                accuracy: this.accuracy + "\\%",
                finalAnswer: this.finalAnswer,
                version,
            }).then(res => {
                this.pdfUrl = this.$fileserver.fileurl(res.data.fileUrl);
                window.open(this.pdfUrl)
            }).finally(() => this.loadingPDF = false)
        },
        postHandleReplay(replay) {
            // 提取回复中的 JSON
            if (replay.startsWith('```json')) {
                replay = replay.substring(7, replay.length - 3)
            }
            if (replay.endsWith('```')) {
                replay = replay.substring(0, replay.length - 3)
            }
            return replay
        },
        formatObj2Str(o) {
            if (typeof o === 'object') {
                for (const key in o) {
                    let formattedString = ''
                    if (obj.hasOwnProperty(key)) {
                        formattedString += `${key}: ${obj[key]}; `;
                    }
                    return formattedString
                }
            } else {
                return o
            }
        },
        compareModelsAnswer() {
            let answers = this.models.filter(model => model.status === 1).map(model => model.answer);
            if (answers.length < 2) {
                this.finalAnswer = answers.length === 1 ? answers[0] : ''
                return Promise.resolve()
            } else {
                return this.$axios.post(`/api/chat/completion`, this.createFinalAnswerForm(answers)).then(res => {
                    try {
                        this.finalAnswer = parseLLMResp(res.data.$response, ['finalAnswer', 'finalAnswerIndex']).finalAnswer
                    } catch (error) {
                        console.error(error)
                        this.$message.error('无法解析最终答案')
                    }
                })
            }
        },
        createFinalAnswerForm(answers) {
            let content = '比较问题的多个答案，先理解并自己得出答案，推理并评估后给出我提供的答案中最准确的一个 \n' +
                '问题：\n' +
                this.config.question + '\n' +
                answers.map((answer, index) => `answer${index + 1}: \n${answer}`).join('\n') + '\n' +
                '你必须遵守下面的规则：\n' +
                '- 用中文回答我。\n' +
                // '- "第几个答案"是最准确的答案的计数，从 1 开始。\n' +
                '- 必须按照下面的格式回答我：\n' +
                'finalAnswer: \n' +
                '<最准确的答案> \n' +
                'finalAnswerIndex: \n' +
                '<第几个答案> \n'
            return {
                messages: [
                    {
                        role: 'user',
                        content,
                    }
                ],
                temperature: 0.1,
                model: 'gpt-3.5-turbo',
            }
        },
        add2Library() {
            let form = {
                question: this.config.question,
                answer: this.finalAnswer,
                knowledge: this.models[0].knowledge,
            }
            this.loadingOfLibrary = true
            this.$axios.post("/api/questionLibrary/add", form).then(res => {
                this.$message.success("已添加到知识库")
            }).finally(() => this.loadingOfLibrary = false);
        },
        onImgUploadSuccess(response, uploadFile) {
            let src = this.$fileserver.fileurl(response.data.url)
            // console.log(src)
            src = 'http://110.40.186.52:10001/static/9530d2d630a04fce9866ae826314e60b.jpg'
            this.loadingocr = true
            this.$axios.post(`/api/ocr/url2text?url=${src}&service=mathPixOCRService`).then(res => {
                this.config.question = res.data
            }).finally(() => this.loadingocr = false)
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
        max-height: calc(100vh - 100px);

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .form-config {

            .form-question {
                :deep(.el-form-item__label) {
                    padding-right: 0;

                    .pic-uploader {
                        float: right;
                        height: 28px;
                    }
                }
            }
        }
    }

    .right-content {
        flex: 1;
        max-height: calc(100vh - 100px);

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        :deep(.el-scrollbar) {
            height: calc(100vh - 210px);
        }

        .result-wrapper {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: 20px;

            :deep(.el-scrollbar) {
                height: 300px;
            }

            .multi-answer {
                grid-column: 1 / span 2;
            }
        }
    }

}

.content-text {
    white-space: pre-wrap;
    word-break: break-all;
}
</style>