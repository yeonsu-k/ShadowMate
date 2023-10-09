import React, { ReactNode } from "react";
import styles from "@styles/mypage/MyPage.module.scss";
import Text from "@components/common/Text";
import SaveIcon from "@mui/icons-material/Save";
import { DeleteOutlined } from "@mui/icons-material";

interface Props {
  children: ReactNode;
  title: string;
  handleSave: (title: string) => void;
  handleDelete: (title: string) => void;
  isDisable: boolean;
}

/**
 * MyPage - 다이어리, 디데이 설정 중 오른쪽 사이드
 */

const MyPageDetail = ({ children, title, isDisable, handleSave, handleDelete }: Props) => {
  const disable = isDisable ? "--disable" : "";
  return (
    <div className={styles["frame__container"]}>
      <div className={styles["frame__title"]}>
        <Text>상세 설정</Text>
      </div>
      {children}
      <div className={styles["frame__button"]}>
        <div className={styles[`frame__button--delete${disable}`]} onClick={() => handleDelete(title)}>
          <DeleteOutlined />
          <Text>삭제</Text>
        </div>
        <div className={styles["frame__button--save"]} onClick={() => handleSave(title)}>
          <SaveIcon />
          <Text>저장</Text>
        </div>
      </div>
    </div>
  );
};

export default MyPageDetail;
