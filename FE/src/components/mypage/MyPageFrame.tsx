import React, { useEffect, useRef, useState } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import MyPageList from "./MyPageList";
import MyPageCategoryItem from "./item/MyPageCategoryItem";
import MyPageDetail from "./MyPageDetail";
import MyPageCategory from "./details/diary/Category";
import MyPageDday from "./details/diary/Dday";
import MyPageDdayItem from "./item/MyPageDdayItem";
import { categoryType, ddayType } from "@util/planner.interface";
import { CATEGORY_LIST, CATEGORY_COLORS } from "@util/data/CategoryData";
import { DDAY_LIST } from "@util/data/DdayData";

interface Props {
  title: string;
}

export interface EditInfoConfig {
  type: string;
  info: categoryType | ddayType | null;
  clicked: number;
}

const MyPageFrame = ({ title }: Props) => {
  /* 카테고리 관련 변수 */
  const [categoryList, setCategoryList] = useState<categoryType[]>(CATEGORY_LIST);
  const [categoryInput, setCategoryInput] = useState<categoryType>({
    categoryId: 0,
    categoryTitle: categoryList[0].categoryTitle,
    categoryEmoticon: categoryList[0].categoryEmoticon,
    categoryColorCode: categoryList[0].categoryColorCode,
  });
  const [categoryClick, setCategoryClick] = useState<number>(0);
  const [colorClick, setColorClick] = useState<number>(0);
  const category_nextId = useRef(categoryList.length);

  /* 디데이 관련 변수 */
  const [ddayList, setDdayList] = useState<ddayType[]>(DDAY_LIST);
  const [ddayClick, setDdayClick] = useState<number>(0);
  const [ddayInput, setDdayInput] = useState<ddayType>({
    ddayId: 0,
    ddayTitle: ddayList[0].ddayTitle,
    ddayDate: ddayList[0].ddayDate,
  });
  const [ddayError, setDdayError] = useState<boolean>(false);
  const dday_nextId = useRef(ddayList.length);

  /* 공통 사용 변수 */
  const [isDisable, setIsDisable] = useState<boolean>(false);

  const handleAdd = (title: string) => {
    if (title === "카테고리") {
      const newCategory: categoryType = {
        categoryId: category_nextId.current,
        categoryTitle: "새 카테고리",
        categoryEmoticon: "",
        categoryColorCode: "#B6DEF7",
      };
      setCategoryList([...categoryList, newCategory]);
      setCategoryInput(newCategory);
      setCategoryClick(categoryList.length);
      category_nextId.current += 1;
    } else {
      const newDday: ddayType = {
        ddayId: dday_nextId.current,
        ddayTitle: "새 디데이",
        ddayDate: new Date(),
      };
      setDdayList([...ddayList, newDday]);
      setDdayClick(ddayList.length);
      dday_nextId.current += 1;
    }
  };

  const handleSave = (title: string) => {
    if (title === "카테고리") {
      setCategoryList(
        categoryList.map((item, idx) => {
          if (categoryInput.categoryId === item.categoryId) {
            return {
              ...item,
              categoryId: categoryInput.categoryId,
              categoryTitle: categoryInput.categoryTitle,
              categoryEmoticon: categoryInput.categoryEmoticon,
              categoryColorCode: CATEGORY_COLORS[colorClick],
            };
          }
          return item;
        }),
      );
    } else {
      if (ddayInput.ddayTitle === "" || ddayInput.ddayTitle.length < 2 || ddayInput.ddayTitle.length > 20) {
        setDdayError(true);
        return;
      }
      setDdayError(false);
      setDdayList(
        ddayList.map((item, idx) => {
          if (ddayInput.ddayId === item.ddayId) {
            return {
              ...item,
              ddayTitle: ddayInput.ddayTitle,
              ddayDate: ddayInput.ddayDate,
            };
          }
          return item;
        }),
      );
    }
  };

  const handleDelete = (title: string) => {
    if (isDisable) return;
    if (title === "카테고리") {
      setCategoryList(
        categoryList.filter((item, idx) => {
          return idx !== categoryClick;
        }),
      );
      // 삭제한 값의 위 (0인 경우 아래) 배열 항목으로 재설정
      setCategoryClick(categoryClick === 0 ? categoryClick : categoryClick - 1);
    } else {
      setDdayList(
        ddayList.filter((item, idx) => {
          return idx !== ddayClick;
        }),
      );
      setDdayClick(ddayClick === 0 ? ddayClick : ddayClick - 1);
    }
  };

  useEffect(() => {
    // 카테고리 및 디데이 항목이 1개 남은 경우, 삭제 불가
    if (title === "카테고리" && categoryList.length <= 1) setIsDisable(true);
    else if (title === "디데이" && ddayList.length <= 1) setIsDisable(true);
    else setIsDisable(false);
  }, [title, categoryList, ddayList]);

  return (
    <div className={styles["frame"]}>
      <MyPageList handleAdd={handleAdd} title={title}>
        {
          {
            카테고리: (
              <>
                {categoryList.map((item, idx) => (
                  <MyPageCategoryItem
                    key={item.categoryId}
                    index={idx}
                    item={item}
                    click={categoryClick}
                    setClick={setCategoryClick}
                  />
                ))}
              </>
            ),
            디데이: (
              <>
                {ddayList.map((item, key) => (
                  <MyPageDdayItem key={key} item={item} index={key} click={ddayClick} setClick={setDdayClick} />
                ))}
              </>
            ),
          }[title]
        }
      </MyPageList>
      <MyPageDetail title={title} isDisable={isDisable} handleSave={handleSave} handleDelete={handleDelete}>
        {
          {
            카테고리: (
              <MyPageCategory
                click={categoryClick}
                categoryList={categoryList}
                input={categoryInput}
                setInput={setCategoryInput}
                colorClick={colorClick}
                setColorClick={setColorClick}
              />
            ),
            디데이: (
              <MyPageDday
                click={ddayClick}
                ddayList={ddayList}
                input={ddayInput}
                setInput={setDdayInput}
                error={ddayError}
              />
            ),
          }[title]
        }
      </MyPageDetail>
    </div>
  );
};

export default MyPageFrame;
