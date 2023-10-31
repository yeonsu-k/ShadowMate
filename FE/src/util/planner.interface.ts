export interface MonthType {
  date: string;
  todoCount: number;
  dayStatus: number;
}

export interface CategoryColorConfig {
  categoryColorId: number;
  categoryColorCode: string;
}

export interface CategoryConfig {
  categoryId: number;
  categoryTitle: string;
  categoryColorCode: string;
  categoryEmoticon?: string | null;
}

export interface TimeTableConfig {
  timeTableId: number;
  startTime: string;
  endTime: string;
}

// 공통 Todo Item
export interface TodoConfig {
  todoId: number;
  category?: CategoryConfig | null;
  todoContent: string;
  todoStatus: "공백" | "완료" | "미완료";
  todoUpdate?: boolean;
  timeTable?: TimeTableConfig | null;
}

/* --- Week Interfaces --- */

export interface WeekTodoItemConfig {
  weeklyTodoId: number;
  weeklyTodoContent: string;
  weeklyTodoStatus: boolean;
  weeklyTodoUpdate?: boolean;
}

export interface ddayType {
  ddayId: number;
  ddayDate: Date | string;
  ddayTitle: string;
}
