import {createSlice} from '@reduxjs/toolkit';

const jobSlice = createSlice({
  name: 'job',
  initialState: {
    jobs: [],
    errors: [],
    isLoading: false,
    currentJob: null
  },
  reducers: {
    jobsRequested: (state) => {
      state.isLoading = true;
    },
    jobRequested: (state) => {
      state.isLoading = true;
    },
    jobFinished: (state, action) => {
      state.isLoading = false;
      state.currentJob = action.payload;
    },
    DeleteJobRequested: (state) => {
      state.isLoading = true;
    },
    DeleteFinished: (state) => {
      state.isLoading = false;
    },
    jobsFinished: (state, action) => {
      state.isLoading = false;

      state.jobs = action.payload.reverse();
    },
    jobsError: (state, action) => {
      state.isLoading = false;
      state.errors = action.paylaod;
    }
  }
});

export const {DeleteJobRequested, DeleteFinished, jobsRequested, jobFinished, jobsFinished, jobRequested, jobsError} = jobSlice.actions;
export const jobReducer = jobSlice.reducer;