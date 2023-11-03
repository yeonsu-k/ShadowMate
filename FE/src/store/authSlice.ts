import { PayloadAction, createSlice } from "@reduxjs/toolkit";
import { rootState } from "@hooks/configStore";

export interface userInfoConfig {
  email: string;
  nickname: string;
  profileImage: string;
  statusMessage: string;
  plannerAccessScope: string;
}

interface authConfig {
  accessToken: string;
  userId: number;
  login: boolean;
  userInfo: userInfoConfig;
}

const initialState: authConfig = {
  accessToken: "",
  userId: 0,
  login: false,
  userInfo: {
    email: "",
    nickname: "",
    profileImage: "",
    statusMessage: "",
    plannerAccessScope: "",
  },
};

const authSlice = createSlice({
  name: "auth",
  initialState,
  reducers: {
    setLogin: (state, { payload }: PayloadAction<{ accessToken: string; userId: number }>) => {
      state.accessToken = payload.accessToken;
      state.login = true;
      state.userId = payload.userId;
    },
    setLogout: (state) => {
      state = initialState;
      localStorage.removeItem("accessToken");
      localStorage.removeItem("id");
    },
    setUserInfo: (state, { payload }: PayloadAction<userInfoConfig>) => {
      state.userInfo = payload;
    },
  },
});

export const { setLogin, setLogout, setUserInfo } = authSlice.actions;
export const selectUserInfo = (state: rootState) => state.auth.userInfo;
export const selectLoginState = (state: rootState) => state.auth.login;
export const selectAccessToken = (state: rootState) => state.auth.accessToken;
export const selectUserId = (state: rootState) => state.auth.userId;
export default authSlice.reducer;
