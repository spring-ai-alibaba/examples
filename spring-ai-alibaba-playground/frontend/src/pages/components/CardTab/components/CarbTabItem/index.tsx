import React from "react";
import { motion } from "framer-motion";

interface CardTabItemProps {
  content?: React.ReactNode;
  key?: string | number;
}

const CardTabItem = (props: CardTabItemProps) => {
  const { content, key } = props;

  return (
    <motion.div
      key={key}
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      transition={{ duration: 0.15 }}
    >
      {content}
    </motion.div>
  );
};

export default CardTabItem;
