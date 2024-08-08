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
                    <el-form-item label="支持模型列表：" prop="aimodel">
                        <el-select v-model="config.aimodel" style="width: 100%;">
                            <el-option label="gpt-4" value="gpt-4" />
                            <el-option label="gpt-3.5-turbo" value="gpt-3.5-turbo" />
                            <el-option label="ERNIE-Bot" value="ERNIE-Bot" />
                            <!-- <el-option label="ERNIE-Bot-turbo" value="ERNIE-Bot-turbo" />
                            <el-option label="CPM hackthon" value="CPM hackthon" />
                            <el-option label="BLOOMZ-7B" value="BLOOMZ-7B" /> -->
                        </el-select>
                    </el-form-item>
                    <el-form-item prop="question">
                        <template #label>
                            <span>题目：</span>
                            <el-button style="margin-left: 120px;" @click="selectLibrary(0)">
                                知识库
                            </el-button>
                        </template>
                        <el-input v-model="config.question" :autosize="{ minRows: 5, maxRows: 7 }" type="textarea"
                            placeholder="请输入题目">
                        </el-input>
                    </el-form-item>
                    <el-form-item label="答案：" prop="answer">
                        <el-input v-model="config.answer" :autosize="{ minRows: 5, maxRows: 7 }" type="textarea"
                            placeholder="请输入正确答案" />
                    </el-form-item>
                    <el-form-item label="知识点：" prop="knowledge">
                        <el-input v-model="config.knowledge" :autosize="{ minRows: 5, maxRows: 7 }" type="textarea"
                            placeholder="请输入知识点" />
                    </el-form-item>
                    <el-form-item prop="related">
                        <template #label>
                            <span>相关题目：</span>
                            <el-button :loading="ocrloading" style="margin-left: 100px;" @click="selectLibrary(1)">
                                知识库
                            </el-button>
                        </template>
                        <el-input v-model="config.related" :autosize="{ minRows: 5, maxRows: 7 }" type="textarea"
                            placeholder="请输入相关题目">
                        </el-input>
                    </el-form-item>
                    <el-form-item label="提示预览：" prop="promot">
                        <el-input v-model="config.promot" :autosize="{ minRows: 5, maxRows: 7 }" type="textarea" />
                    </el-form-item>
                </el-form>
            </el-scrollbar>
        </el-card>
        <el-card class="right-content">
            <template #header>
                <div class="card-header">
                    <span>内容</span>
                    <div>
                        <el-upload ref="upload" style="display: inline-block; margin-right: 12px;" :show-file-list="false"
                            class="button" :action="$fileserver.uploadUrl" :multiple="true" :on-success="onImgUploadSuccess"
                            accept="image/*" :auto-upload="true">
                            <template #trigger>
                                <el-button type="primary">添加文件</el-button>
                            </template>
                        </el-upload>
                        <el-button class="button" type="primary" @click="addAnswer">添加答案</el-button>
                        <el-button class="button" type="primary" @click="batchCheck">批改</el-button>
                        <el-button class="button" type="primary" @click="clearData">清空</el-button>
                    </div>
                </div>
            </template>
            <div class="result-wrapper" v-loading="loading">
                <el-table :data="tableData" style="width: 100%; user-select: text;" class="main-table" empty-text="请添加文件">
                    <template v-for="column in columns" :key="column.prop">
                        <el-table-column v-bind="column">
                            <template v-if="column.prop === 'file'" #default="scope">
                                <div class="file-wrapper" v-if="scope.row.fileurl">
                                    <el-text truncated style="margin-bottom: 10px;">{{ scope.row.filename }}</el-text>
                                    <div style="display: flex; align-items: center">
                                        <el-image :src="scope.row.fileurl" :preview-src-list="[scope.row.fileurl]"
                                            preview-teleported style="width: 180px; max-height: 200px;"
                                            fit="contain"></el-image>
                                    </div>
                                </div>
                            </template>
                            <template v-else-if="column.prop === 'status'" #default="scope">
                                <el-text style="white-space: pre-wrap; word-break: break-all;"
                                    :type="statusType(scope.row.state)">{{ scope.row.status }}</el-text>
                            </template>
                            <template v-else-if="column.prop === 'operations'" #default="scope">
                                <el-button type="primary" style="width: 80px; margin-left: 0" plain size="small"
                                    @click="doCheck(scope.row)">批改</el-button>
                                <el-button type="primary" style="width: 80px; margin-left: 0" plain size="small"
                                    @click="delData(scope.$index)">删除</el-button>
                                <el-dropdown v-if="scope.row.state === 2" style="margin-left: 0"
                                    @command="(v) => createAndDownloadPDF(scope.row, v)">
                                    <el-button :loading="loadingPDF" style="width: 80px; " plain size="small"
                                        @click="createAndDownloadPDF(scope.row)" type="primary">
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
                                <el-button v-if="scope.row.state === 2" type="primary" style="width: 80px; margin-left: 0"
                                    plain size="small" @click="add2Library(scope.row)">添加到知识库</el-button>
                            </template>
                            <template v-else-if="column.prop === 'formatted'" #default="scope">
                                <el-scrollbar max-height="200px" v-if="scope.row.state === 2">
                                    <el-row><el-text>{{ "是否正确：" + scope.row.trueText }}</el-text></el-row>
                                    <el-row><el-text v-if="scope.row.errText" type="danger">{{ "错误：" + scope.row.errText
                                            }}</el-text></el-row>
                                    <el-row><el-text>{{ "评价：" + scope.row.comment }}</el-text></el-row>
                                </el-scrollbar>
                            </template>
                            <template v-else-if="column.type === 'textarea'" #default="scope">
                                <el-scrollbar max-height="200px">
                                    <el-text style="white-space: pre-wrap; word-break: break-all;">
                                        {{ scope.row[column.prop] }}
                                    </el-text>
                                </el-scrollbar>
                            </template>
                        </el-table-column>
                    </template>
                </el-table>
            </div>
        </el-card>
        <library-dialog :isShow="isShowLibrary" @onClose="onLibraryClose" @onSelected="onLibrarySelected"></library-dialog>
        <answer-dialog :isShow="isShowAnswer" @onClose="onAnswerClose"></answer-dialog>
    </div>
    <!-- <pdfviewer id="pdf-dom" :data="selectedData" v-show="showPDFViewer"></pdfviewer> -->
    <!-- <latex-viewer></latex-viewer> -->
</template>

<script>
import Pdfviewer from './pdfviewer.vue'
import { getPdf } from '../../utils/htmlToPdf'
import { nextTick } from 'vue'
import LatexViewer from './latexviewer.vue'
import LibraryDialog from './library-dialog.vue'
import AnswerDialog from './answer-dialog.vue'
import { ElMessageBox } from 'element-plus'
export default {
    components: {
        Pdfviewer,
        LatexViewer,
        LibraryDialog,
        AnswerDialog
    },
    data() {
        return {
            config: {
                aimodel: 'gpt-4',
                question: '',
                answer: '',
                promot: '根据我提供的问题和正确答案，核对学生答案是否正确：\n' +
                    '题目: ${question} \n' +
                    '正确答案: ${answer} \n' +
                    '学生答案: ${myanswer}',
                knowledge: '',
                related: '',
            },
            configRules: {
                aimodel: [
                    { required: true, message: '请选择支持模型', trigger: 'blur' }
                ],
                question: [
                    { required: true, message: '请输入题目', trigger: 'blur' }
                ],
                answer: [
                    { required: true, message: '请输入正确答案', trigger: 'blur' }
                ],
                promot: [
                    { required: true, message: '请输入提示信息', trigger: 'blur' }
                ],
            },
            columns: [
                {
                    prop: 'file',
                    label: '文件',
                    width: 150,
                },
                {
                    prop: 'ocrText',
                    label: '我的答案',
                    width: 150,
                    type: 'textarea',
                },
                {
                    prop: 'aiText',
                    label: 'AI批改结果',
                    width: 150,
                    type: 'textarea',
                },
                {
                    prop: 'formatted',
                    label: '格式化结果',
                    width: 150,
                },
                {
                    prop: 'status',
                    label: '状态',
                    width: 150,
                    type: 'textarea',
                },
                {
                    prop: 'operations',
                    label: '操作',
                    align: 'center',
                    width: 110,
                }
            ],
            tableData: [],
            checkIndex: 0,
            selectedData: {},
            showPDFViewer: false,
            loading: false,
            isShowLibrary: false,
            libraryType: '', // 知识库赋值类型 0-题目 1-相关题目
            isShowAnswer: false,
            ocrloading: false,
        }
    },
    methods: {
        onImgUploadSuccess(response, uploadFile) {
            this.tableData.push({
                filename: uploadFile.name,
                fileurl: this.$fileserver.fileurl(response.data.url),
                // fileurl: 'https://ocr-demo-1254418846.cos.ap-guangzhou.myqcloud.com/general/GeneralAccurateOCR/GeneralAccurateOCR2.jpg',
                ocrText: "",
                aiText: "",
                status: "",
                state: 0, // 0: 未批改， 1: 批改中， 2: 批改完成  3: 批改失败
            })
        },
        handleExceed(files, fileList) {
            this.$message.warning(`当前限制选择 5 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
        },
        clearData() {
            this.tableData = []
        },
        delData(index) {
            this.tableData.splice(index, 1)
        },
        doCheck(data, next) {
            data = Object.assign(data, {
                state: 0,
                status: "",
                aiText: "",
                pdfUrl: '',
            })
            this.$refs.configForm.validate((valid, fields) => {
                if (valid) {
                    let op;
                    if (data.ocrText) {
                        data.status = "正在AI批改..."
                        op = Promise.resolve();
                    } else {
                        data.state = 1
                        data.status = "正在ocr识别..."
                        op = this.ocrForText(data).then(res => {
                            data.ocrText = res.data
                            data.status = "正在AI批改..."
                        }, err => {
                            data.state = 3
                            data.status = `ocr识别失败: ${err.message}`
                            return Promise.reject(err)
                        })
                    }
                    op.then(() => {
                        return this.aicheck(data).then(res => {
                            data.aiText = this.postHandleReplay(res.data.$response)

                            try {
                                let parsed = JSON.parse(data.aiText);
                                data = Object.assign(data, parsed, {
                                    trueText: parsed.isCorrect ? '正确' : '错误',
                                    isTrue: parsed.isCorrect ? 1 : 0,
                                })
                            } catch (error) {
                                data.isTrue = -1
                            }

                            console.log(data)

                            data.status = "批改完成"
                            data.state = 2
                        }, err => {
                            data.state = 3
                            data.status = `AI批改失败: ${err.message}`
                        })
                    }).finally(() => {
                        if (next) {
                            next();
                        }
                    })
                } else {
                    this.$message.error('请完善设置信息')
                }
            })
        },
        matchAIText(text, regex) {
            const matches = text.match(regex);
            if (matches && matches.length >= 2) {
                return matches[1]; // 匹配到的内容位于索引为 1 的位置
            } else {
                return ""; // 如果未匹配到则返回空字符串
            }
        },
        ocrForText(data) {
            // return Promise.resolve({ data: "这是ocr识别的结果" })
            return this.$axios.post(`/api/ocr/url2text?url=${data.fileurl}`);
        },
        /* aicheck(data) {
            let form = [
                {
                    role: 'user',
                    content: this.replaceText(this.config.promot, {
                        question: this.config.question,
                        answer: this.config.answer,
                        myanswer: data.ocrText,
                    })
                }
            ]
            return this.$axios.post(`/api/openai/completionForMessage?model=${this.config.aimodel}&temperature=1`, form);
        }, */
        aicheck(data) {
            let form = {
                model: this.config.aimodel,
                messages: [
                    {
                        role: 'user',
                        content: this.replaceText(this.config.promot, {
                            question: this.config.question,
                            answer: this.config.answer,
                            myanswer: data.ocrText,
                            knowledge: this.config.knowledge,
                        }) + '\n' +
                            '先根据题目来得出答案，然后通过分析学生的答案，来与正确答案进行比较，得出你的结果。\n' +
                            '你需要回复我学生的答案是否正确，学生答案的错误以及你对于学生答案的评价。\n' +
                            '学生答案正确通过 Boolean 类型来表示；错误以及评价请使用 Latex 格式的文本来表示。\n' +
                            'Replay me in chinese。\n' +
                            'Check if the JSON syntax format of the response is correct; Provide output in JSON format as follows: \n' +
                            '{"isCorrect":${是否正确},"errText":"${错误}","comment":"${评价}"}'
                    },
                ],
                temperature: 0.1,
            }
            return this.$axios.post(`/api/chat/completion`, form);
        },
        replaceText(text, ctx) {
            return text.replace(/\${(\w+)}/g, function (match, key) {
                return ctx[key] || match;
            });
        },
        statusType(state) {
            if (state === 1) {
                return "info"
            } else if (state === 2) {
                return "success"
            } else if (state === 3) {
                return "danger"
            }
        },
        batchCheck() {
            this.$refs.configForm.validate((valid, fields) => {
                if (valid) {
                    this.$confirm('批量批改作业?', '提示', {
                        confirmButtonText: '确定',
                        cancelButtonText: '取消',
                        type: 'warning'
                    }).then(() => {
                        this.checkIndex = 0
                        this.doBatchCheck()
                    })
                } else {
                    this.$message.error('请完善设置信息')
                }
            })
        },
        doBatchCheck() {
            this.doCheck(this.tableData[this.checkIndex], () => {
                this.checkIndex++
                if (this.checkIndex < this.tableData.length) {
                    this.doBatchCheck()
                } else {
                    this.$message.success("批改完成")
                }
            })
        },
        showPDF(data) {
            this.selectedData = Object.assign({}, data)
            this.showPDFViewer = true
            // this.selectedData.ocrText = "根据给定的题目，我将用正式的语调，以中等长度写一篇关于“随便写点东西”的文章。随便写点东西。这个题目似乎是一个非常开放的主题，给予了我充分的自由发挥的空间。在这个主题下，我可以随心所欲地表达我的思想，观点和各种内容。然而，我要确保我的文章保持有逻辑性和连贯性，不离题太远。随便写点东西。这个简短却多义的题目似乎带有一种不加约束和任性的意味。在日常生活中，有时候我们都会希望自己能够不受束缚地表达自己的观点和想法。然而，在现实中，我们需要时刻记住，自由带来的责任性也同样重要。当我们随便写东西时，可能会没有明确的目标或目的。这样的写作可能会缺乏方向性和条理性，无法将读者从头到尾吸引住。因此，即使我们在随便写东西时，我们也可以建立一个大纲或者大致的结构来指导我们的写作。通过这种方式，我们可以保持文章的连贯性，并且更好地向读者传达我们的思想。另外，随便写东西也表达了一种放松和自由的状态。有时候我们的思绪会跳跃，一下子涌现出各种各样的想法。与其试图将这些想法限制到一个特定的主题或观点上，不如将其随意地书写下来。这种随意性有时可以帮助我们在表达中更加真实和自然。然而，在我们随便写东西时，也要注意不要过分放肆。我们应该对自己的言辞负责，确保我们的言论不会引发不必要的争议或伤害到他人。此外，我们还要警惕不要陷入敷衍了事的陷阱，而忽视了写作的质量和有效性。总而言之，“随便写点东西”可能看起来是一个不受限制和自由的主题，但我们仍然需要在写作中保持逻辑性和连贯性。通过建立一个大致的结构和遵循一定的准则，我们可以更好地进行自由表达，并确保我们的观点得到准确传达。同时，我们要记住，在自由中也需要对自己的言辞负责，避免不必要的争议和伤害。随便写点东西，可以成为我们放松的方式，更好地表达自己的想法和情感。但在不受限制的表达中，我们需要保持价值观和道德的边界，以确保我们的言论不仅自由，而且负责任。无论我们随便写什么样的内容，都应该以一种平和、理性和尊重的态度对待。在世界紧张和纷乱的时刻，我们也需要冷静地思考和反思。随便写点东西可能是我们思绪的一种释放和纾解，但我们也要意识到，在言辞和行动之间，总是有一个界限。总而言之，随便写点东西既是一种自由的表达方式，也是对自己思考能力的一种挑战。在我们享受创作的自由和奇妙时，我们也要保持谨慎和责任感。无论我们随便写什么样的东西，都应该追求真实、准确和积极的表达，以推动我们个人和社会的发展。"
            // this.selectedData.aiText = "根据给定的题目，我将用正式的语调，以中等长度写一篇关于“随便写点东西”的文章。随便写点东西。这个题目似乎是一个非常开放的主题，给予了我充分的自由发挥的空间。在这个主题下，我可以随心所欲地表达我的思想，观点和各种内容。然而，我要确保我的文章保持有逻辑性和连贯性，不离题太远。随便写点东西。这个简短却多义的题目似乎带有一种不加约束和任性的意味。在日常生活中，有时候我们都会希望自己能够不受束缚地表达自己的观点和想法。然而，在现实中，我们需要时刻记住，自由带来的责任性也同样重要。当我们随便写东西时，可能会没有明确的目标或目的。这样的写作可能会缺乏方向性和条理性，无法将读者从头到尾吸引住。因此，即使我们在随便写东西时，我们也可以建立一个大纲或者大致的结构来指导我们的写作。通过这种方式，我们可以保持文章的连贯性，并且更好地向读者传达我们的思想。另外，随便写东西也表达了一种放松和自由的状态。有时候我们的思绪会跳跃，一下子涌现出各种各样的想法。与其试图将这些想法限制到一个特定的主题或观点上，不如将其随意地书写下来。这种随意性有时可以帮助我们在表达中更加真实和自然。然而，在我们随便写东西时，也要注意不要过分放肆。我们应该对自己的言辞负责，确保我们的言论不会引发不必要的争议或伤害到他人。此外，我们还要警惕不要陷入敷衍了事的陷阱，而忽视了写作的质量和有效性。总而言之，“随便写点东西”可能看起来是一个不受限制和自由的主题，但我们仍然需要在写作中保持逻辑性和连贯性。通过建立一个大致的结构和遵循一定的准则，我们可以更好地进行自由表达，并确保我们的观点得到准确传达。同时，我们要记住，在自由中也需要对自己的言辞负责，避免不必要的争议和伤害。随便写点东西，可以成为我们放松的方式，更好地表达自己的想法和情感。但在不受限制的表达中，我们需要保持价值观和道德的边界，以确保我们的言论不仅自由，而且负责任。无论我们随便写什么样的内容，都应该以一种平和、理性和尊重的态度对待。在世界紧张和纷乱的时刻，我们也需要冷静地思考和反思。随便写点东西可能是我们思绪的一种释放和纾解，但我们也要意识到，在言辞和行动之间，总是有一个界限。总而言之，随便写点东西既是一种自由的表达方式，也是对自己思考能力的一种挑战。在我们享受创作的自由和奇妙时，我们也要保持谨慎和责任感。无论我们随便写什么样的东西，都应该追求真实、准确和积极的表达，以推动我们个人和社会的发展。"
            nextTick(() => {
                getPdf("批改结果", 'pdf-dom')
                this.showPDFViewer = false
            })
        },
        createAndDownloadPDF(data, version = 'v2') {
            let filename = '';
            if (data.filename) {
                filename = data.filename.split('.')[0]
            }

            ElMessageBox.prompt('', '文件名称', {
                confirmButtonText: '确认',
                cancelButtonText: '取消',
                inputValue: filename,
            }).then(res => {
                const filename = `${res.value}.pdf`
                /* if (data.pdfUrl) {
                    this.downloadFile(data.pdfUrl, filename)
                    return
                } */

                this.loading = true
                this.$axios.post(`/api/pdf/createPDF`, Object.assign({ version }, data, this.config)).then(res => {
                    let url = this.$fileserver.fileurl(res.data.fileUrl);
                    data.pdfUrl = url;
                    this.downloadFile(data.pdfUrl, filename)
                }).finally(() => {
                    this.loading = false
                });
            })
        },
        selectLibrary(type) {
            this.libraryType = type
            this.isShowLibrary = true
        },
        onLibrarySelected(library) {
            if (this.libraryType === 0) {
                this.config.question = library.question
                this.config.answer = library.answer
                this.config.knowledge = library.knowledge
            }
            if (this.libraryType === 1) {
                this.config.related = library.question
            }
            this.onLibraryClose()
        },
        onLibraryClose() {
            this.isShowLibrary = false
        },
        addAnswer() {
            this.isShowAnswer = true
        },
        onAnswerClose(answer) {
            if (answer) {
                let data = {
                    ocrText: answer,

                    aiText: "",
                    status: "",
                    state: 0, // 0: 未批改， 1: 批改中， 2: 批改完成  3: 批改失败
                }
                this.tableData.push(data)
            }
            this.isShowAnswer = false;
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
        downloadFile(url, name) {
            // 使用fetch获取文件内容
            fetch(url)
                .then(response => response.blob())
                .then(blob => {
                    // 如果需要下载，可以使用前面提到的下载代码
                    const a = document.createElement("a");
                    a.style.display = "none";
                    a.href = URL.createObjectURL(blob);
                    a.download = name;
                    document.body.appendChild(a);
                    a.click();
                    window.URL.revokeObjectURL(a.href);
                })
                .catch(error => {
                    console.error('发生错误:', error);
                });
        },
        add2Library(data) {
            ElMessageBox.prompt('', '名称', {
                confirmButtonText: '确认',
                cancelButtonText: '取消',
            }).then(res => {
                let form = {
                    name: res.value,
                    question: this.config.question,
                    correctAnswer: this.config.answer,
                    knowledge: this.config.knowledge,
                    answer: data.ocrText,
                    isTrue: data.isTrue,
                    errText: data.errText,
                    comment: data.comment,
                    aiContent: data.aiText,
                }
                this.$axios.post("/api/review/add", form).then(res => {
                    this.$message.success("已添加到知识库")
                })
            })
        }
    }
}
</script>

<style lang="scss" scoped>
.container-wrapper {
    width: auto !important;
    display: flex;
    column-gap: 20px;
    height: 100%;

    .left-bar {
        width: 300px;
        height: 100%;

        .card-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        :deep(.el-scrollbar) {
            height: calc(100vh - 210px);
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
    }

}

.result-wrapper {
    .file-wrapper {
        display: flex;
        flex-direction: column;
        align-items: center;
        padding: 10px;
    }

    .main-table {
        height: calc(100vh - 210px);
    }
}
</style>