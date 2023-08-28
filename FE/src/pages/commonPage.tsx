import React, { useState } from "react";
import Text from "@components/common/Text";
import Button from "@components/common/Button";

const commonPage = () => {
  return (
    <>
      <div>
        <Text type="small">프리텐다드 - small</Text>
        <br /> <br />
        <Text type="default">프리텐다드 - default</Text>
        <br /> <br />
        <Text type="semi-medium">프리텐다드 - semi-medium</Text>
        <br /> <br />
        <Text type="medium">프리텐다드 - medium</Text>
        <br /> <br />
        <Text type="semi-large">프리텐다드 - semi-large </Text>
        <br /> <br />
        <Text type="large">프리텐다드 - large</Text>
      </div>
      <div>
        <Button type="blue">소설공유</Button>
        <Button children="♥ 50" type="red" />
        <Button children="일간 보기" type="gray" />
        <Button children="로그아웃" type="gray" />
      </div>
    </>
  );
};

export default commonPage;
