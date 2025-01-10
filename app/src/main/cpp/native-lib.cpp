#include <jni.h>
#include <string>
#include <mutex>
#include <thread>

#include <cstdlib>
#include <cassert>
#include <cstdio>
#include <chrono>
#include <sstream>
#include <vector>
#include <array>

#include <locale>
#include <codecvt>

#include <cstdint>
#include <ctime>

#include <android/log.h>
#include "utf8.h"
#include "Test.h"
#include "thread_pool.hpp"
#include "code_nodes.hpp"
#include "script_runtime.hpp"


using namespace smart;
std::mutex mtx;

static void abc(int g, int k, int n) {

}

template<typename Base, typename T>
inline bool instanceof(const T *ptr) {
    return dynamic_cast<const Base *>(ptr) != nullptr;
}

namespace doorlang {

    typedef int(*handle_character_proc)(int next_character);

    int handle1(int a) {
        int g = a * a * a * a;
        return g;
    }

    /*
     * 次の文字に対する処理を実行する関数を
     * 関数ポインタで保持。
     */
    static handle_character_proc state_proc;

    /*
    auto handle1 = [](int a){
        return 5;
    };
     */
    static void SetStateProc(handle_character_proc next_proc) {
        state_proc = next_proc;
    }

    static inline jobject &&generateCodeDocumentForKotlin(JNIEnv *env) {

        jclass arrayClass = env->FindClass("org/rokist/videolistplayer/models/Line");
        //jclass arrayClass = env->FindClass("java/lang/Object");
        jmethodID initMethod = env->GetMethodID(arrayClass, "<init>", "()V");

        jobject line = env->NewObject(arrayClass, initMethod);



        //jmethodID addMethod = env->GetMethodID(arrayClass, "add", "(Ljava/lang/Object;)Z");
        //jobject myArray = env->NewObject(arrayClass, initMethod);


        jfieldID fieldId = env->GetFieldID(arrayClass, "pointer", "J");
        //jlong value = env->GetLongField(line, fieldId);
        env->SetLongField(line, fieldId, (jlong) 3);
//        } else {
//            int *pval = (int *) value;
//            __android_log_print(ANDROID_LOG_DEBUG, "aaa", "Test  : text = %d", *pval);
//        }

        std::string text = u8R"(class a {} fn funcA{})";
        const char *chars = text.c_str();
        //doc.fromCodeText(chars, 0, text.size());
/*
        for (auto *node : doc.rootNodes) {
            if (auto vl = instanceof<ClassNode>(node)) {
                __android_log_print(ANDROID_LOG_DEBUG, "aaa", "Test  : is ClassNode");
            } else if (auto vl = instanceof<FuncNode>(node)) {
                __android_log_print(ANDROID_LOG_DEBUG, "aaa", "Test  : is funcNode");
            }
        }
 */

/*
        std::string text_from_parser = "";

        auto* codeLine = doc.firstCodeLine;
        while (codeLine) {
            for (auto &&node : codeLine->nodes) {
                text_from_parser += node->textWithoutChildren();
            }
            //GLOG << "\\" << std::endl;
            codeLine = codeLine->nextLine;
        }
 */


        return std::move(line);

    }


    static JNIEnv *globalEnv = nullptr;
    static jclass jvmCodeNodeClass = nullptr;
    static jmethodID jvmCodeNodeCtor = nullptr;
    static jclass CodeNodeType = nullptr;

    static jmethodID settypeMethod = nullptr;

    static jfieldID indentField3 = nullptr;
    static jclass lineClass = nullptr;
    static jfieldID firstCodeNodeField = nullptr;
    static jfieldID nextCodeNodeField = nullptr;
    static jfieldID jvmCodeNodeTextField = nullptr;
    static jmethodID lineCtor = nullptr;
    static jfieldID textField = nullptr;
    static jfieldID nextLineField = nullptr;
    static jfieldID linePointerFieldId =  nullptr;//env->GetFieldID(lineClass, "pointer", "J");
    static jfieldID codeNodePointerFieldId2 = nullptr;
    static jfieldID codeNodePrevSpacesFieldId = nullptr;

    static inline void prepareClassAndFields(JNIEnv *env) {
        jvmCodeNodeClass = env->FindClass("org/rokist/videolistplayer/models/CodeNode");
        jvmCodeNodeCtor = env->GetMethodID(jvmCodeNodeClass, "<init>", "()V");
        CodeNodeType = env->FindClass("org/rokist/videolistplayer/models/CodeNodeType");

        settypeMethod = env->GetMethodID(jvmCodeNodeClass, "setType", "(II)V");

        //"Lorg/rokist/canlang/models/CodeNodeType;");


        lineClass = env->FindClass("org/rokist/videolistplayer/models/Line");
        indentField3 = env->GetFieldID(lineClass, "indent", "I");
        linePointerFieldId = env->GetFieldID(lineClass, "pointer", "J");


        firstCodeNodeField = env->GetFieldID(lineClass, "firstCodeNode",
                                             "Lorg/rokist/canlang/models/CodeNode;");
        nextCodeNodeField = env->GetFieldID(jvmCodeNodeClass, "nextCodeNode",
                                            "Lorg/rokist/canlang/models/CodeNode;");

        jvmCodeNodeTextField = env->GetFieldID(jvmCodeNodeClass, "text", "Ljava/lang/String;");
        codeNodePointerFieldId2 = env->GetFieldID(jvmCodeNodeClass, "pointer", "J");
        codeNodePrevSpacesFieldId = env->GetFieldID(jvmCodeNodeClass, "prevSpaces", "I");


        lineCtor = env->GetMethodID(lineClass, "<init>", "()V");
        textField = env->GetFieldID(lineClass, "text", "Ljava/lang/String;");

        nextLineField = env->GetFieldID(lineClass, "next",
                                        "Lorg/rokist/canlang/models/Line;");

    }

    /*
     * sig Java
        Z 	boolean
        B 	byte
        C 	char
        S 	short
        I 	int
        J 	long
        F 	float
        D 	double
        Lfully-qualified-class; 	Class
        [type 	ARray
        (arg-types)ret-type 	Method
     */


    std::pair<int, std::string> &&f() {
        auto val = (std::pair<int, std::string>{3, "Hello"});
        return std::move(val);
    }
    //auto &&g = f();

    static void updateLineInfo(JNIEnv *env, CodeLine *line, jobject jvmLine) {

        auto *node = line->firstNode;
        auto *prevJvmNode = (jobject) nullptr;
        while (node) {

            jobject jvmCodeNode = env->NewObject(jvmCodeNodeClass, jvmCodeNodeCtor);
            env->SetLongField(jvmCodeNode, codeNodePointerFieldId2, (jlong) ((intptr_t) node));
            env->SetIntField(jvmCodeNode, codeNodePrevSpacesFieldId, (jint)node->prev_chars);

            {
                char *chs = DocumentUtils::getTextFromNode(node);
                //__android_log_print(ANDROID_LOG_DEBUG, "aaa", "parse %d = %s", node->vtable->nodeTypeId, chs);

                {
                    jstring javaResult = env->NewStringUTF(chs);
                    env->SetObjectField(jvmCodeNode, jvmCodeNodeTextField, javaResult);
                    env->DeleteLocalRef(javaResult);
                }

                int parentType = node->parentNode == nullptr ? 0 : (int)node->parentNode->vtable->nodeTypeId;
                env->CallVoidMethod(jvmCodeNode, settypeMethod, static_cast<jint>(node->vtable->nodeTypeId),
                                    static_cast<jint>(parentType));

                if (prevJvmNode == nullptr) {
                    env->SetObjectField(jvmLine, firstCodeNodeField, jvmCodeNode);
                } else {
                    env->SetObjectField(prevJvmNode, nextCodeNodeField, jvmCodeNode);
                }

            }

            auto *temp = prevJvmNode;
            prevJvmNode = jvmCodeNode;
            if (temp) {
                env->DeleteLocalRef(temp);
            }

            node = node->nextNodeInLine;
        }
        if (prevJvmNode) {
            env->DeleteLocalRef(prevJvmNode);
        }
    }


    static jobject createJvmDocument(JNIEnv *env, DocumentStruct *doc) {
        jclass documentClass = env->FindClass("org/rokist/videolistplayer/models/CodeDocument");
        jmethodID initMethod = env->GetMethodID(documentClass, "<init>", "()V");
        jobject jvmDoc = env->NewObject(documentClass, initMethod);


        //__android_log_print(ANDROID_LOG_DEBUG, "aaa", "createJvmDocument = %d", 3);

        {
            // update pointer to doc
            jfieldID fieldId = env->GetFieldID(documentClass, "pointer", "J");
            env->SetLongField(jvmDoc, fieldId, (jlong) ((intptr_t) doc));
        }

        auto *prevJvmLine = (jobject) nullptr;
        auto *line = doc->firstCodeLine;
        while (line) {
            jobject jLine = env->NewObject(lineClass, lineCtor);
            env->SetIntField(jLine, indentField3, line->depth);
            // update pointer to line
            env->SetLongField(jLine,linePointerFieldId,(jlong) ((intptr_t) line));

            {
                if (line == doc->firstCodeLine) {
                    jfieldID fieldId = env->GetFieldID(documentClass, "firstLine",
                                                       "Lorg/rokist/canlang/models/Line;");
                    env->SetObjectField(jvmDoc, fieldId, jLine);
                }

                if (prevJvmLine) {
                    env->SetObjectField(prevJvmLine, nextLineField, jLine);

                    jobject assignedNext = env->GetObjectField(prevJvmLine, nextLineField);
                    if (env->IsSameObject(assignedNext, jLine)) {
                    }
                    env->DeleteLocalRef(assignedNext);
                }

                updateLineInfo(env, line, jLine);
            }

            if (prevJvmLine) {
                env->DeleteLocalRef(prevJvmLine);
            }
            prevJvmLine = jLine;
            line = line->nextLine;
        }

        if (prevJvmLine) {
            env->DeleteLocalRef(prevJvmLine);
        }

        {
            jfieldID fieldId = env->GetFieldID(documentClass, "lineNums", "J");
            env->SetLongField(jvmDoc, fieldId, (jlong) doc->lineCount);
        }

        return jvmDoc;
    }


    void actionCreator(void *node1, void *node2, int actionRequest) {
        if (globalEnv == nullptr) {
            return;
        }

        JNIEnv *env = globalEnv;
        if (actionRequest == EventType::CreateDocument) { // create document

        } else if (actionRequest == EventType::FirstLineChanged) { // changed first line of document
            //auto *doc = (DocumentStruct *) (node1);
            //createJvmDocument(env, doc);

        } else if (actionRequest == EventType::CreateNode) { // create node

        } else if (actionRequest == EventType::CreateLine) { // create line


        } else if (actionRequest == 4) { // tap line

        }
    }

    static jobject line2 = nullptr;
    static jobject prevJvmDoc = nullptr;


    extern "C" JNIEXPORT jobject JNICALL
    Java_org_rokist_canlang_MainActivity_createDocument(
            JNIEnv *env, jobject /* this */, /*jobject line, */jbyteArray bytes, jlong count) {


//        {
//
//
//            constexpr char text[] = R"(
//fn main() {
//    let a = 0
//    int b = 0
//    print("test日本語")
//}
//)";
//
//            const char *chars = text;// .c_str();
//            auto *document = Alloc::newDocument(DocumentType::CodeDocument, nullptr);
//            DocumentUtils::parseText(document, chars, sizeof(text) - 1);
//            DocumentUtils::generateHashTables(document);
//
//            char *treeText = DocumentUtils::getTextFromTree(document);
//            //printf("%s\n", treeText);
//
//            startScript(document);
//        }
//

















        //jobject &&jobj = generateCodeDocumentForKotlin(env);

        //env->GetDirectBufferAddress();
        jbyte *te_aa = env->GetByteArrayElements(bytes, 0);

        globalEnv = env;
        prepareClassAndFields(env);

        auto start = std::chrono::high_resolution_clock::now();













        ScriptEnv* scriptEnv = ScriptEnv::loadScript((char *) te_aa, static_cast<size_t>(count));
        if (scriptEnv->document->context->syntaxErrorInfo.hasError) {
        }

        scriptEnv->validateScript();
        if (scriptEnv->context->logicErrorInfo.hasError) {
        }
        //int ret = scriptEnv->runScriptEnv();
        auto *document = scriptEnv->document;











        //const char *chars = text.c_str();
        //auto *document = Alloc::newDocument(DocumentType::CodeDocument, actionCreator);
        //DocumentUtils::parseText(document, (char *) te_aa, static_cast<size_t>(count));

        auto elapsed = std::chrono::high_resolution_clock::now() - start;
        long long millisec = std::chrono::duration_cast<std::chrono::milliseconds>(elapsed).count();

        //__android_log_print(ANDROID_LOG_DEBUG, "aaa", "parse time = %d", millisec);


        //DocumentUtils::assignIndents(document);

/*
        DocumentUtils::performCodingOperation(CodingOperations::AutoIndentSelection
                , document, Cast::upcast(document->firstRootNode), Cast::upcast(&document->endOfFile));
*/

        jobject jvmDoc = createJvmDocument(env, document);


        Alloc::deleteDocument(document);
        ScriptEnv::deleteScriptEnv(scriptEnv);

        globalEnv = nullptr;


        //char *treeText = DocumentUtils::getTextFromTree(document);

        env->ReleaseByteArrayElements(bytes, te_aa, 0);

        {
            jclass documentClass = env->FindClass("org/rokist/videolistplayer/models/CodeDocument");

            jclass codeNodeClass = env->FindClass("org/rokist/videolistplayer/models/CodeNode");
        }

        jclass arrayClass = env->FindClass("org/rokist/videolistplayer/models/Line");

        //jclass arrayClass = env->FindClass("java/lang/Object");
        jmethodID initMethod = env->GetMethodID(arrayClass, "<init>", "()V");


        //jmethodID addMethod = env->GetMethodID(arrayClass, "add", "(Ljava/lang/Object;)Z");
        //jobject myArray = env->NewObject(arrayClass, initMethod);



//        jfieldID fieldId = env->GetFieldID(arrayClass, "pointer", "J");
//        env->GetLongField(line, fieldId);
//        if (value == 0) {
//            //auto *p = new int();
//            //*p = 55;
//            //env->SetLongField(line, fieldId, (jlong) p);
//        } else {
//            //int *pval = (int *) value;
//            //__android_log_print(ANDROID_LOG_DEBUG, "aaa", "Test  : text = %d", *pval);
//        }

        if (line2 == nullptr) {
            jobject line3 = env->NewObject(arrayClass, initMethod);
            line2 = env->NewGlobalRef(line3);
        }

/*
        if (prevJvmDoc) {
            env->DeleteGlobalRef(prevJvmDoc);
        }
        prevJvmDoc = env->NewGlobalRef(jvmDoc);
*/
        return jvmDoc;
    }


    extern "C" JNIEXPORT jlong JNICALL
    Java_org_rokist_canlang_MainActivity_donothing(
            JNIEnv *env,
            jobject /* this */) {

        return 3;

    }

    extern "C" JNIEXPORT jobject JNICALL
    Java_org_rokist_canlang_MainActivity_performCodingOperation(
            JNIEnv *env,
            jobject /* this */, jint op) {

        return nullptr;
    }


    extern "C" JNIEXPORT jobject JNICALL
    Java_org_rokist_canlang_MainActivity_getLineFromIndex(
            JNIEnv *env,
            jobject /* this */,
            jlong linIndex
    ) {

        return nullptr;
    }



    extern "C" JNIEXPORT jstring JNICALL
    Java_org_rokist_canlang_MainActivity_stringFromJNI(
            JNIEnv *env,
            jobject /* this */) {

        long long ti = funca();


        std::string hello = "Hello from C++ 1";
        std::unique_ptr<int> a;
        return env->NewStringUTF(hello.c_str());
    }

    long prev_time = 0;

    extern "C" JNIEXPORT jlong JNICALL
    Java_org_rokist_canlang_MainActivity_stringFromJNI2(
            JNIEnv *env,
            jobject /* this */,
            jlong addrGray
    ) {
        jintArray result;
        result = env->NewIntArray(25);

        std::string hello = "Hello from C++ 4";
        unsigned int G_NumOfCores = std::thread::hardware_concurrency();

        Test2::g = G_NumOfCores;

        long long ti = funca();
        Test2::g = ti;

        prev_time = ti;
        long a = addrGray;

        return Test2::g;// env->NewStringUTF(hello.c_str());
    }


    std::wstring JavaToWSZ(JNIEnv *env, jstring string) {
        std::wstring value;
        if (string == NULL) {
            return value; // empty string
        }
        const jchar *raw = env->GetStringChars(string, NULL);
        if (raw != NULL) {
            jsize len = env->GetStringLength(string);
            //value.assign(raw, len);
            value.assign(raw, raw + len);
            env->ReleaseStringChars(string, raw);
        }
        return value;
    }


// convert wstring to UTF-8 string
    std::string wstring_to_utf8(const std::wstring &str) {
        std::wstring_convert<std::codecvt_utf8<wchar_t>> myconv;
        return myconv.to_bytes(str);
    }

    extern "C" JNIEXPORT jlong JNICALL
    Java_org_rokist_canlang_MainActivity_doWithText(
            JNIEnv *env,
            jobject /* this */,
            jstring str
    ) {
        jintArray result;
        result = env->NewIntArray(25);


        jboolean isCopy = JNI_FALSE;
        auto *text = env->GetStringUTFChars(str, &isCopy);
        //jsize len = env->GetStringLength(str);
        jsize len = env->GetStringUTFLength(str);

        std::string strin{text};
        auto *g = L"iofw";
        std::wstring st = JavaToWSZ(env, str);


        char *twochars = (char *) text;
        char *w = twochars;
        utf8::advance(w, 0, w + len + 1);
        auto valid = utf8::is_valid(w, w + len);


        int alen = len;

        std::vector<char> chs;
//    chs.push_back(0xEF);
//    chs.push_back(0xBB);
//    chs.push_back(0xBF);

        for (int i = 0; true; i++) {
            auto ch = text[i];
            if (ch == '\0') {
                __android_log_print(ANDROID_LOG_DEBUG, "Tag", "Test : %d, %d", i, alen);

                break;
            }

            if ((ch & 0x80) != 0x80) {
                //if (ch >> 7 == 0) {
//        if (ch  <= 0x7F) {
                __android_log_print(ANDROID_LOG_DEBUG, "Tag", "Test : ch = %c", ch);
            } else {
                chs.push_back(ch);
            }
        }
        chs.push_back('\0');
        char *a = &chs[0];
        __android_log_print(ANDROID_LOG_DEBUG, "Tag", "Test  : text = %s", a);




//    char* w2 = twochars+4;
//    int cp = utf8::next(w2, twochars + len+38);
        //assert (cp == 0x65e5);
//    0x65e5/assert (w == twochars + 3);













        //assert (w == twochars + 5);

//    __android_log_print(ANDROID_LOG_DEBUG, "Tag", "Test  : text = %s %d", w, cp);

        std::string hello = "Hello from C++ 4";

        unsigned int G_NumOfCores = std::thread::hardware_concurrency();

        return 55;
    }


}