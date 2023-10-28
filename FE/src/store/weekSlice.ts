import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { TodoConfig, WeekTodoItemConfig } from "@util/planner.interface";

export interface DayListConfig {
  date: string;
  retrospection: string | null;
  dailyTodo: TodoConfig[] | null;
}

export interface WeekConfig {
  plannerAccessScope: string;
  dday: string;
  weeklyTodo: WeekTodoItemConfig[];
  dayList: DayListConfig[];
}

const initialState: WeekConfig = {
  plannerAccessScope: "",
  dday: "",
  weeklyTodo: [],
  dayList: [],
};

const weekSlice = createSlice({
  name: "week",
  initialState,
  reducers: {
    setWeekInfo: (state, { payload }: PayloadAction<WeekConfig>) => {
      state.plannerAccessScope = payload.plannerAccessScope;
      state.dday = payload.dday;
      state.weeklyTodo = payload.weeklyTodo;
      state.dayList = payload.dayList;
    },
  },
});

export const { setWeekInfo } = weekSlice.actions;
export const selectDayList = (state: rootState) => state.week.dayList;

export default weekSlice.reducer;
