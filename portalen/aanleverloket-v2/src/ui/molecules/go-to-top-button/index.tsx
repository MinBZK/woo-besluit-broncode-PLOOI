import { useEffect, useState } from "react";
import { IconButtonMolecule } from "../button";
import styles from "./styles.module.scss";

interface Props {
  id: string;
  icon: string;
  type: "default" | "primary" | "orange" | "blue";
  text?: string;
}

export function GoToTopButtonMolecule(props: Props) {
  const [showTopBtn, setShowTopBtn] = useState(false);

  useEffect(() => {
    window.addEventListener("scroll", () => {
      if (window.scrollY > 300) {
        setShowTopBtn(true);
      } else {
        setShowTopBtn(false);
      }
    });
  }, []);

  const toTop = () => {
    window.scrollTo({ top: 0, left: 0, behavior: "smooth" });
  };

  return (
    <>
      {showTopBtn && (
        <div className={styles.container}>
          <IconButtonMolecule {...props} onClick={toTop} title="Go to top" />
        </div>
      )}
    </>
  );
}
