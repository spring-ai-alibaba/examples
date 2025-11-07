import React from "react";
import { motion, useInView } from "framer-motion";
import { ReactNode, useRef } from "react";

interface AnimatedSectionProps {
  children: ReactNode;
  className?: string;
  delay?: number;
  threshold?: number;
}

const AnimatedSection = (props: AnimatedSectionProps) => {
  const { children, className, delay = 0, threshold = 0.2 } = props;
  const ref = useRef(null);
  const isInView = useInView(ref, {
    once: true,
    amount: threshold,
  });

  return (
    <motion.div
      ref={ref}
      initial={{ opacity: 0, y: 75 }}
      animate={{ opacity: isInView ? 1 : 0, y: isInView ? 0 : 75 }}
      transition={{ duration: 0.5, delay }}
    >
      {children}
    </motion.div>
  );
};
export default AnimatedSection;
