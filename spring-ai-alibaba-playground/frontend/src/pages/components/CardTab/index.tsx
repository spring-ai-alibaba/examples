import React from "react";
import { Card, CardProps, TabsProps, Flex } from "antd";
import { useStyles } from "./style";
import { AnimatePresence } from "framer-motion";
import CardTabItem from "./components/CarbTabItem";
export interface CardTabItem {
  key: string;
  label: React.ReactNode;
  children: React.ReactNode;
  disabled?: boolean;
}

export interface CardTabProps {
  title?: React.ReactNode;
  defaultActiveKey?: string;
  activeKey?: string;
  items: CardTabItem[];
  onTabChange?: (activeKey: string) => void;
  cardProps?: Omit<CardProps, "title">;
  tabsProps?: Omit<TabsProps, "items" | "activeKey" | "onChange">;
  className?: string;
  style?: React.CSSProperties;
}

const CardTab: React.FC<CardTabProps> = ({
  title,
  activeKey,
  onTabChange,
  items,
  cardProps,
  className,
}) => {
  const { styles } = useStyles();

  const activeItem = items?.find((item) => item.key === activeKey);

  const handleTabChange = (newActiveKey: string) => {
    onTabChange?.(newActiveKey);
  };

  return (
    <Card
      title={title}
      className={`${styles.cardTab} ${className || ""}`}
      {...cardProps}
    >
      <Flex align="center" justify="space-between">
        {items.map((item, index) => (
          <div
            key={"tabItem" + item.key + index}
            className={
              item.key === activeKey
                ? styles.activeCardTabItem
                : styles.cardTabItem
            }
            onClick={() => {
              if (!item.disabled) {
                handleTabChange(item.key);
              }
            }}
          >
            {item.label}
          </div>
        ))}
      </Flex>

      <div className={styles.cardTabContent}>
        <AnimatePresence mode="wait">
          <CardTabItem key={activeKey} content={activeItem?.children} />
        </AnimatePresence>
      </div>
    </Card>
  );
};

export default CardTab;
