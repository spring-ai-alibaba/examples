import React from "react";
import { useStyle } from "../../style";

const PureText = (props: {
  style?: React.CSSProperties;
  children: React.ReactNode;
}) => {
  const { styles } = useStyle();
  const { style = {}, children } = props;
  return (
    <div className={styles.textWithoutMargin} style={{ ...style }}>
      {children}
    </div>
  );
};

export default PureText;
