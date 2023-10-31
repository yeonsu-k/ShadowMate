import { createSlice, PayloadAction } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";
import { TodoConfig, TimeTableConfig } from "@util/planner.interface";
import dayjs from "dayjs";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
import isSameOrAfter from "dayjs/plugin/isSameOrAfter";
dayjs.extend(isSameOrBefore);
dayjs.extend(isSameOrAfter);

interface dayInfoConfig {
  plannerAccessScope: "전체공개" | "친구공개" | "비공개";
  dday: string | null;
  like: boolean;
  likeCount: number;
  retrospectionImage: string | null;
  dailyTodos: TodoConfig[];
}

interface dayConfig {
  date: string;
  info: dayInfoConfig;
  todoItem: TodoConfig; // 내가 선택한 todo
}

const initialState: dayConfig = {
  date: dayjs().format("YYYY-MM-DD"),
  info: {
    plannerAccessScope: "전체공개",
    dday: null,
    like: false,
    likeCount: 0,
    retrospectionImage: null,
    dailyTodos: [],
  },
  todoItem: {
    todoId: 0,
    todoContent: "",
    todoStatus: "공백",
    category: {
      categoryId: 0,
      categoryTitle: "",
      categoryColorCode: "#E9E9EB",
      categoryEmoticon: "",
    },
    timeTable: {
      timeTableId: 0,
      startTime: "",
      endTime: "",
    },
  },
};

const daySlice = createSlice({
  name: "plannerDay",
  initialState,
  reducers: {
    setDayInfo: (state, action: PayloadAction<dayConfig["info"]>) => {
      state.info = action.payload;
    },
    setDayRetrospectionImage: (state, action: PayloadAction<dayInfoConfig["retrospectionImage"]>) => {
      state.info.retrospectionImage = action.payload;
    },
    setDate: (state, action: PayloadAction<dayConfig["date"]>) => {
      state.date = action.payload;
    },
    setTodoItem: (state, action: PayloadAction<dayConfig["todoItem"]>) => {
      state.todoItem = action.payload;
    },
    removeTodoItem: (state) => {
      state.todoItem = initialState.todoItem;
    },
    setTodoList: (state, action: PayloadAction<dayInfoConfig["dailyTodos"]>) => {
      state.info.dailyTodos = action.payload;
    },
    setTimeTable: (state, action: PayloadAction<{ todoId: number; startTime: string; endTime: string }>) => {
      const { todoId, startTime, endTime } = action.payload;

      const tempArr = state.info.dailyTodos.map((item) => {
        if (startTime != "" && item.timeTable && item.timeTable.startTime != "") {
          if (
            !(
              dayjs(item.timeTable.endTime).isSameOrBefore(startTime) ||
              dayjs(item.timeTable.startTime).isSameOrAfter(endTime)
            )
          ) {
            return { ...item, timeTable: initialState.todoItem.timeTable };
          }
        }
        return item;
      });

      const findIndex = tempArr.findIndex((item) => item.todoId == todoId);
      if (tempArr[findIndex].timeTable) {
        const timeTableInfo = tempArr[findIndex].timeTable as TimeTableConfig;
        tempArr[findIndex].timeTable = { ...timeTableInfo, startTime, endTime };
      }

      state.info.dailyTodos = tempArr;
    },
  },
});

export const BASIC_TODO_ITEM = initialState.todoItem!;
export const BASIC_CATEGORY_ITEM = initialState.todoItem.category!;
export const { setDayInfo, setDayRetrospectionImage, setDate, setTodoItem, removeTodoItem, setTodoList, setTimeTable } =
  daySlice.actions;
export const selectDate = (state: rootState) => state.day.date;
export const selectDayInfo = (state: rootState) => state.day.info;
export const selectTodoItem = (state: rootState) => state.day.todoItem;
export const selectTodoList = (state: rootState) => state.day.info.dailyTodos;

export default daySlice.reducer;
