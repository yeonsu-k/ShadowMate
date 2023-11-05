import React from "react";
import { rootState } from "@hooks/configStore";
import { PayloadAction, createSlice } from "@reduxjs/toolkit";

interface ModalConfig {
  isOpen: boolean;
}

const initialState: ModalConfig = {
  isOpen: false,
};

const modalSlice = createSlice({
  name: "modal",
  initialState,
  reducers: {
    setModalOpen: (state, { payload }: PayloadAction<() => void>) => {
      state.isOpen = true;
    },
    setModalClose: (state, { payload }: PayloadAction<() => void>) => {
      state.isOpen = false;
    },
  },
});

export const { setModalOpen, setModalClose } = modalSlice.actions;
export const selectModal = (state: rootState) => state.modal;

export default modalSlice.reducer;
