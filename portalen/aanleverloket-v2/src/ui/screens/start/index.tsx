import { useNavigate } from "react-router-dom";
import { ContainerAtom, IconAtom } from "../../atoms";
import { DefaultLayout } from "../../layouts";
import { ButtonMolecule, JumbotronMolecule } from "../../molecules";
import {
  OverheidFooterOrganism,
  OverheidHeaderOrganism,
  ToastOrganism,
} from "../../organisms";

export default function StartScreen() {
  const navigate = useNavigate();

  return (
    <DefaultLayout
      header={
        <>
          <ToastOrganism />
          <OverheidHeaderOrganism />
        </>
      }
      body={
        <div>
          <JumbotronMolecule
            center
            header={"Aanleverloket"}
            link={
              <ButtonMolecule
                text={"Document uploaden"}
                id={"nav-aanleveren-btn"}
                title={"Document uploaden"}
                onClick={() => navigate("/aanleveren")}
                type={"primary"}
              />
            }
            intro={
              "U bent ingelogd in het aanleverloket van het Platform Open Overheidsinformatie. Hier kunt u documenten uploaden, wijzigen of depubliceren."
            }
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
                  <div
                    style={{
                      flexDirection: "row",
                      display: "flex",
                      gap: "10px",
                      alignItems: "center",
                    }}
                  >
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
                <a
                  target={"_blank"}
                  href="https://www.koopoverheid.nl/voor-overheden/rijksoverheid/plooi-platform-open-overheidsinformatie"
                >
                  <div
                    style={{
                      flexDirection: "row",
                      display: "flex",
                      gap: "10px",
                      alignItems: "center",
                    }}
                  >
                    Ga naar het portaal
                    <IconAtom
                      icon={"icon-cta-right"}
                      size={"medium"}
                      alt={"update"}
                    />
                  </div>
                </a>
              }
              intro={
                "U bent ingelogd in het aanleverloket van het Platform Open Overheidsinformatie. Hier kunt u documenten uploaden, wijzigen of depubliceren."
              }
            />
          </ContainerAtom>
        </div>
      }
    />
  );
}
