import React from "react";
import { useStyles } from "../../style";

const CodeInfo = () => {
  const { styles } = useStyles();

  return (
    <div className={`${styles.codeInfoContainer} ${styles.card}`}>
      <div className={styles.codeInfoBody}>
        <div className={styles.codeInfoIntro}>
          <h4>RAG (Retrieval-Augmented Generation)</h4>
          <p>
            RAG
            是一种结合检索和生成的技术，它能够从知识库中检索相关信息，并基于这些信息生成回答。
          </p>
        </div>

        <div className={styles.codeInfoSteps}>
          <div className={styles.codeInfoStepItem}>
            <div className={styles.codeInfoStepTitle}>
              <span className={styles.codeInfoTitleText}>1. 知识库检索</span>
            </div>
            <div className={styles.codeInfoStepDesc}>
              系统会从预定义的知识库中检索与用户问题相关的信息。
            </div>
          </div>

          <div className={styles.codeInfoStepItem}>
            <div className={styles.codeInfoStepTitle}>
              <span className={styles.codeInfoTitleText}>2. 信息整合</span>
            </div>
            <div className={styles.codeInfoStepDesc}>
              将检索到的相关信息进行整合和排序，确保回答的准确性和相关性。
            </div>
          </div>

          <div className={styles.codeInfoStepItem}>
            <div className={styles.codeInfoStepTitle}>
              <span className={styles.codeInfoTitleText}>3. 生成回答</span>
            </div>
            <div className={styles.codeInfoStepDesc}>
              基于整合后的信息，生成自然、准确的回答。
            </div>
          </div>
        </div>

        {/* <div className={styles.documentationContainer}>
          <h4>使用示例</h4>
          <p>你可以尝试询问以下类型的问题：</p>
          <ul>
            <li>什么是 RAG？</li>
            <li>RAG 的工作原理是什么？</li>
            <li>RAG 有哪些应用场景？</li>
          </ul>
        </div> */}
      </div>
    </div>
  );
};

export default CodeInfo;
