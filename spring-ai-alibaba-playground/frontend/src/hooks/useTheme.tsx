import { useAtom } from "jotai";
import { useEffect } from "react";
import {
  actualThemeAtom,
  setupThemeListener,
  themeModeAtom,
  ThemeMode,
} from "../stores/theme.store";

export const useTheme = () => {
  const [themeMode, setThemeMode] = useAtom(themeModeAtom);
  const [actualTheme] = useAtom(actualThemeAtom);

  // 在组件挂载时设置监听器和读取 localStorage
  useEffect(() => {
    // 从 localStorage 读取主题模式
    const storedThemeMode = localStorage.getItem("themeMode");

    if (storedThemeMode) {
      setThemeMode(storedThemeMode as ThemeMode);
    } else {
      // 如果 localStorage 为空，设置为默认值
      setThemeMode("light"); // 或 'dark'，取决于你的默认值
    }

    // 设置主题监听器
    if (themeMode === "system") {
      return setupThemeListener(() => {
        setThemeMode("system");
      });
    }
  }, [themeMode, setThemeMode]);

  // 切换主题的函数
  const toggleTheme = () => {
    const newTheme = themeMode === "dark" ? "light" : "dark";
    setThemeMode(newTheme);
    localStorage.setItem("themeMode", newTheme); // 保存到 localStorage
  };

  // 设置特定主题的函数
  const setTheme = (mode: ThemeMode) => {
    setThemeMode(mode);
    localStorage.setItem("themeMode", mode); // 保存到 localStorage
  };

  return {
    themeMode,
    actualTheme,
    toggleTheme,
    setTheme,
  };
};
