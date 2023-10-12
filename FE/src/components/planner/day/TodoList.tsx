import React, { useEffect, useMemo, useRef, useState } from "react";
import styles from "@styles/planner/day.module.scss";
import TodoItem from "@components/planner/day/TodoItem";
import { todoType } from "@util/planner.interface";
import { todoData_list } from "@util/data/DayTodos";

export const TodoDataDefalut: todoType = {
  todoId: 0,
  categoryTitle: "",
  categoryColorCode: "#E9E9EB",
  todoContent: "",
  todoStatus: 0,
};

interface Props {
  clicked: boolean;
}

const TodoList = () => {
  const [todos, setTodos] = useState<todoType[]>(todoData_list);
  const todoListSize = useMemo(() => {
    return todos.length + 1 >= 12 ? todos.length + 1 : 12;
  }, [todos]);
  const todoEndRef = useRef<HTMLDivElement>(null);
  const nextId = useRef(todos.length + 1);

  useEffect(() => {
    if (todos.length + 1 >= 12 && todoEndRef.current) {
      todoEndRef.current.scrollTop = todoEndRef.current.scrollHeight;
    }
  }, [todos.length]);

  const todoModule = (() => {
    const insertTodo = (props: todoType) => {
      setTodos([...todos, { ...props, todoId: nextId.current }]);
      nextId.current += 1;
    };

    const updateTodo = (idx: number, props: todoType) => {
      let copyTodos = [...todos];
      copyTodos[idx] = { ...props };
      setTodos(copyTodos);
    };

    const deleteTodo = (idx: number) => {
      let newTodos = todos.filter((item, i) => idx != i);
      setTodos(newTodos);
    };

    return {
      insertTodo,
      updateTodo,
      deleteTodo,
    };
  })();

  return (
    <div
      ref={todoEndRef}
      className={styles["todo-list"]}
      style={{ gridTemplateRows: `repeat(${todoListSize}, calc(100%/12)` }}
    >
      {todos.map((todo, idx) => (
        <TodoItem key={todo.todoId} idx={idx} item={todo} todoModule={todoModule} />
      ))}
      <TodoItem addTodo item={TodoDataDefalut} todoModule={todoModule} />
      {Array.from({ length: 11 - todos.length }).map((item, idx) => (
        <TodoItem key={idx} disable item={TodoDataDefalut} todoModule={todoModule} />
      ))}
    </div>
  );
};

export default TodoList;
