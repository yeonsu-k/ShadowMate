import React, { useState } from "react";
import Header from "@components/planner/day/Header";
import styles from "@styles/planner/day.module.scss";
import Ment from "@components/planner/day/Ment";
import TodoTable from "@components/planner/day/TodoTable";

const DayPage = () => {
  const [ment, setMent] = useState({
    todayGoals: "",
    tomorrowGoals: "",
    retrospections: "",
  });

  const handleInput = (e: React.ChangeEvent<HTMLTextAreaElement>) => {
    setMent({
      ...ment,
      [e.target.name]: e.target.value,
    });
  };

  const { todayGoals, tomorrowGoals, retrospections } = ment;

  return (
    <div className={styles["page-container"]}>
      <Header />

      <Ment title={"오늘의 다짐"} name="todayGoals" value={todayGoals} onChange={handleInput} rows={1} maxLength={50} />

      <div className={styles["item__todo-list"]}>
        <TodoTable />
      </div>

      <Ment
        title={"오늘의 회고"}
        name="retrospections"
        value={retrospections}
        onChange={handleInput}
        rows={5}
        maxLength={100}
        fileImg
      />

      <div className={styles["item__driver"]} />

      <div className={styles["item__time"]}>
        <span>{4}</span>
        <span>시간</span>
        <span>{10}</span>
        <span>분</span>
      </div>

      <div className={styles["item__timetable"]}>
        <div>타임테이블</div>
      </div>

      <Ment
        title={"내일 다짐"}
        name="tomorrowGoals"
        value={tomorrowGoals}
        onChange={handleInput}
        rows={5}
        maxLength={50}
      />
    </div>
  );
};

export default DayPage;
