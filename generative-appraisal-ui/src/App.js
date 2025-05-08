import React from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import { ThemeProvider, createTheme } from "@mui/material";

import HomePage from "./components/HomePage";
import Header from "./components/Header";
import Footer from "./components/Footer";

import styles from "./components/HomePage/index.module.css";

const theme = createTheme({
  palette: {
    primary: {
      main: "#006a4d",
      dark: "#024731",
      light: "#649c00",
    },
  },
  typography: {
    fontFamily: "Poppins, sans-serif",
  },
  components: {
    MuiCssBaseline: {
      styleOverrides: `
        @font-face {
          font-family: 'Poppins';
        }
      `,
    },
    MuiButton: {
      styleOverrides: {
        root: {
          textTransform: "capitalize",
        },
      },
    },
    MuiTab: {
      styleOverrides: {
        root: {
          "&.Mui-selected": {
            color: "#006a4d",
          },
        },
      },
    },
    MuiTabs: {
      defaultProps: {
        TabIndicatorProps: {
          style: { backgroundColor: "#006a4d", color: "#006a4d" },
        },
      },
    },
    MuiInput: {
      styleOverrides: {
        root: {
          "&:hover:not(.Mui-disabled, .Mui-error):before": {
            borderColor: "#649c00",
          },
        },
      },
    },
    MuiTextField: {
      styleOverrides: {
        root: {
          "& .MuiOutlinedInput-root:hover .MuiOutlinedInput-notchedOutline": {
            borderColor: "#006a4d",
          },
          "& .MuiOutlinedInput-root.Mui-focused  .MuiOutlinedInput-notchedOutline":
          {
            borderColor: "#006a4d",
          },
        },
      },
    },
  },
});

function App() {
  return (
    <div className={styles.root}>
      <Router>
        <ThemeProvider theme={theme}>
          <Header />
          <Routes>
            <Route path="/" exact element={<HomePage />} />=
          </Routes>
          <Footer />
        </ThemeProvider>
      </Router>
    </div>
  );
}

export default App;
