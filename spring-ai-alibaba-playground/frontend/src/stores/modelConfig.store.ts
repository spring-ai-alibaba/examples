import { atom, useAtom } from "jotai";
import { getModels } from "../api/base";
import { ModelOption } from "../types";

const modelOptionListAtom = atom<ModelOption[]>([]);
const currentModelAtom = atom<ModelOption>();

export const useModelConfigContext = () => {
  const [modelOptionList, setModelOptionList] = useAtom(modelOptionListAtom);
  const [currentModel, setCurrentModel] = useAtom(currentModelAtom);

  const updateModelOptionList = (list: ModelOption[]) => {
    setModelOptionList(list);
  };

  const chooseModel = (modelName: string) => {
    console.log("chooseModel", modelName, modelOptionList);
    const targetModel = modelOptionList.find(
      (item) => item.value === modelName
    );

    const formattedModel = {
      label: targetModel?.label || modelName,
      value: targetModel?.value || modelName,
      desc: targetModel?.desc || "",
    };

    setCurrentModel(formattedModel);
  };

  const initModelOptionList = async () => {
    const list = await getModels();
    console.log("list", list);
    const formattedList = Array.from(list).map((item) => {
      return {
        label: item.model,
        value: item.model,
        desc: item.desc,
      };
    });
    updateModelOptionList(formattedList);
    chooseModel(formattedList[0].value);
  };

  return {
    currentModel,
    modelOptionList,
    chooseModel,
    setModelOptionList,
    initModelOptionList,
  };
};
