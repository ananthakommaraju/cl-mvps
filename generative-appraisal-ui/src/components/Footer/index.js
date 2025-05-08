import { Typography } from "@mui/material";

import styles from "./index.module.css";
// import { useSession } from "../../context/SessionContext";

const Footer = () => {
  // const { count } = useSession();

  return (
    <footer className={styles.footer}>
      {/* <Typography style={{ flexGrow: 1 }}>
        Active User - {count}
      </Typography> */}
      <Typography variant="body2">
        © {new Date().getFullYear()}{" "}
        <a href="https://lloydstechnologycentre.com" target="_blank" rel="noreferrer">
          Lloyds Technology Centre
        </a>{" "}
        - All rights reserved.
      </Typography>
    </footer>
  );
};

export default Footer;
