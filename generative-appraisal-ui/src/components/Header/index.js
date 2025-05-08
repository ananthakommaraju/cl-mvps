import { AppBar, Toolbar, Typography } from "@mui/material";

import styles from "./index.module.css";
import newLogo from "../../media/images/newLogo.png";
// import { useSession } from "../../context/SessionContext";
import { useNavigate } from "react-router-dom";

const Header = () => {
  const navigate = useNavigate();
  return (
    <AppBar
      position="static"
      style={{ backgroundColor: "#006a4d" }}
    >
      <Toolbar onClick={() => navigate("/")}>
        <img src={newLogo} alt="Logo" className={styles.logo} />
        <Typography variant="h6" style={{ flexGrow: 1 }}>
          Status Report
        </Typography>
      </Toolbar>
    </AppBar>
  );
};

export default Header;
