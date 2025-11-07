import { atom, useAtom } from "jotai";
import { FunctionMenuItem } from "../types";

const activeMenuPageAtom = atom<string | null>(null);
const inputtingContentAtom = atom<string | null>(null);
const menuCollapsedAtom = atom(false);

export enum MenuPage {
  Chat = "chat",
  ImageGen = "image-gen",
  DocSummary = "doc-summary",
  MultiModal = "multi-modal",
  ToolCalling = "tool-calling",
  Rag = "rag",
  Mcp = "mcp",
  MoreExamples = "more-examples",
}

export const useFunctionMenuStore = () => {
  const [activeMenuPage, setActiveMenuPage] = useAtom(activeMenuPageAtom);
  const [inputtingContent, setInputtingContent] = useAtom(inputtingContentAtom);
  const [menuCollapsed, setMenuCollapsed] = useAtom(menuCollapsedAtom);

  const updateInputtingContent = (content: string) => {
    console.log("Store: 更新输入内容 ->", content);
    // 确保 content 是有效的字符串
    setInputtingContent(typeof content === "string" ? content : "");
  };

  const updateActiveMenuPage = (menuPageItem: FunctionMenuItem) => {
    setInputtingContent(null);
    setActiveMenuPage(menuPageItem.key);
  };

  const chooseActiveMenuPage = (key: MenuPage) => {
    setActiveMenuPage(key);
  };

  const toggleMenuCollapsed = () => {
    setMenuCollapsed(!menuCollapsed);
  };

  return {
    activeMenuPage,
    inputtingContent,
    menuCollapsed,
    updateActiveMenuPage,
    chooseActiveMenuPage,
    updateInputtingContent,
    toggleMenuCollapsed,
  };
};
