import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { useAppDispatch, useAppSelector } from "../../../store/hooks";
import { logout, redirectLogin } from "../../../store/reducers/auth";
import { selectAuth } from "../../../store/selectors";
import {
  ContainerAtom,
  HeaderAtom,
  LogoAtom,
  DesktopHiddenAtom,
  MobileHiddenAtom,
  IconAtom,
} from "../../atoms";
import { IExternalLink } from "../../interfaces/Link";
import { IconButtonMolecule, ListMolecule } from "../../molecules";
import styles from "./styles.module.scss";

interface Props {
  links: IExternalLink[];
  logo: string;
  subtitle: string;
}

export function HeaderMolecule(props: Props) {
  const state = useAppSelector(selectAuth);
  const dispatch = useAppDispatch();
  const navigate = useNavigate();

  const [menuOpened, setMenuOpened] = useState(false);

  let classNameNav = `${styles.header__nav}`;

  if (menuOpened) {
    classNameNav += ` ${styles[`header__nav--menuOpened`]}`;
  }

  return (
    <>
      <HeaderAtom>
        <div className={styles.header__start}>
          <ContainerAtom centered type="row">
            <div>
              <LogoAtom alt="Overheid logo" size="large" src={props.logo} />
              <p>{props.subtitle}</p>
            </div> 
            <ContainerAtom type="row">
              <div className={styles.header__start__right}>
                {
                state.isAuthenticated && <>
                    <IconAtom icon={"icon-profile"} size={"medium"} alt={"icon-profile"} />
                    <p>{state.organizations[0]?.label}</p>
                    <IconButtonMolecule id={"uitloggen-button"} title="Uitloggen" onClick={() => {dispatch(logout()); } } text={"Uitloggen"} type={"default"} icon={"icon-logout"}  />

                  </>
                }         
                {
                state.isAuthenticated === false && <>
                    <IconButtonMolecule id={"inloggen-button"} title="Inloggen" onClick={() => { dispatch(redirectLogin()); } } text={"Inloggen"} type={"default"} icon={"icon-logout"} />
                  </>
                }         
                <DesktopHiddenAtom>
                  <IconButtonMolecule
                    rtl
                    id="icon-hamburger"
                    title="Menu"
                    text="Menu"
                    type="default"
                    icon={"icon-hamburger"}
                    onClick={() => {setMenuOpened(!menuOpened);}}
                  />
                </DesktopHiddenAtom>
              </div>
            </ContainerAtom>
          </ContainerAtom>
        </div>
        <nav className={classNameNav}>
          <ContainerAtom centered>
            <MobileHiddenAtom>
              <div className={styles.flex}>
                <ListMolecule unstyled links={props.links} />
              </div>
            </MobileHiddenAtom>

            <DesktopHiddenAtom>
              {menuOpened && (
                  <ListMolecule unstyled links={props.links} />
              )}
            </DesktopHiddenAtom>
          </ContainerAtom>
        </nav>
      </HeaderAtom>
    </>
  );
}
