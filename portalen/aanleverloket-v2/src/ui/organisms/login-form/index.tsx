import { useAppDispatch, useAppSelector } from "../../../store/hooks";
import { selectAuth } from "../../../store/selectors";
import { redirectLogin } from "../../../store/reducers/auth";
import { ContainerAtom, IconAtom } from "../../atoms";
import { ButtonMolecule, JumbotronMolecule } from "../../molecules";
import styles from "./styles.module.scss";

export function LoginFormOrganism() {
  const dispatch = useAppDispatch();
  const { fetching } = useAppSelector(selectAuth);
  const onAuthenticate = () => dispatch(redirectLogin());

  return (
    <div>
      <JumbotronMolecule
        center
        header={"Aanleverloket"}
        link={
          <ButtonMolecule
            id="inloggen"
            title={"Inloggen"}
            loading={fetching}
            text="Inloggen"
            type="primary"
            onClick={onAuthenticate}
          />
        }
        intro={[
          "Welkom op het aanleverloket van het Platform Open Overheidsinformatie. Hier kunt u publieke informatie aanleveren die vindbaar wordt gemaakt voor burgers zoals omschreven in de Wet open overheid.",
          " Log in met uw account om voor uw overheidsorganisatie documenten actief openbaar te maken.",
        ]}
      />
      <ContainerAtom type="row">
        <JumbotronMolecule
          subheader={"Aanleverloket"}
          header={"Laatste nieuws"}
          link={
            <a
              target={"_blank"}
              href="https://www.koopoverheid.nl/voor-overheden/rijksoverheid/plooi-platform-open-overheidsinformatie"
            >
              <div className={styles.row}>
                Lees meer
                <IconAtom
                  icon={"icon-cta-right"}
                  size={"medium"}
                  alt={"update"}
                />
              </div>
            </a>
          }
          intro={
            "Ga naar de website van KOOP voor de laatste nieuwsbrieven, de FAQ, technische documentatie en handleidingen over aanleverloket van het Platform Open Overheid."
          }
        />
        <JumbotronMolecule
          subheader={"Portaal"}
          header={"Openbaarheid documenten"}
          link={
            <a target={"_blank"} href="https://open.overheid.nl">
              <div className={styles.row}>
                Ga naar het portaal
                <IconAtom
                  icon={"icon-cta-right"}
                  size={"medium"}
                  alt={"update"}
                />
              </div>
            </a>
          }
          intro={[
            `Welkom bij het aanleverloket van het Platform Open Overheidsinformatie. Hier kunt u publieke informatie aanleveren die vindbaar wordt gemaakt voor burgers zoals omschreven in de Wet open overheid.`,
            `Log in met uw account om voor uw overheidsorganisatie documenten actief openbaar te maken.`,
          ]}
        />
      </ContainerAtom>
    </div>
  );
  // return <div className={styles.loginForm}>
  //     <FormAtom>
  //         <ButtonMolecule id="inloggen" loading={fetching} text="Inloggen" type="primary" onClick={onAuthenticate} />
  //     </FormAtom>
  //     <SpacerAtom space={4} />
  // </div>
}
