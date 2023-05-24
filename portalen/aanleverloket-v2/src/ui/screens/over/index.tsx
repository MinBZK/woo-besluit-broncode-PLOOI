import { HeadingH3Atom } from "../../atoms";
import { SmallLayout } from "../../layouts";
import {
  OverheidFooterOrganism,
  OverheidHeaderOrganism,
  ToastOrganism,
} from "../../organisms";

export default function OverScreen() {

  return (
    <SmallLayout
      header={
        <>
          <ToastOrganism />
          <OverheidHeaderOrganism />
        </>
      }
      body={
        <div>
          <i>
          Over het aanleverloket voor het Platform voor Open Overheidsinformatie
          </i>
          <HeadingH3Atom>De Wet open overheid</HeadingH3Atom>
          <p>
          De Wet open overheid verplicht een groot aantal overheidsorganisaties om publieke informatie actief openbaar te maken. Documenten die actief openbaar moeten worden gemaakt op het Platform Open Overheidsinformatie dienen aangeleverd te worden via een automatische systeemkoppeling of handmatig via het aanleverloket. 
          </p>
          <HeadingH3Atom>Het aanleverloket</HeadingH3Atom>
          <p>
          Het aanleverloket is een website die kan worden gebruikt voor het handmatig uploaden van bestanden en bijbehorende metadata. Deze gegevens worden vervolgens verwerkt door PLOOI en gecontroleerd (denk hierbij aan virussen, opslag en indexering). Wanneer deze gegevens als correct worden bevonden wordt het document zichtbaar op het Platform Open Overheidsinformatie. Hiernaast kunt u het aanleverloket gebruiken om bestanden te bewerken, depubliceren of opnieuw te publiceren.
          </p>
          <HeadingH3Atom>Wanneer maak ik gebruik van het aanleverloket?</HeadingH3Atom>
          <p>
          Verwacht u dat het actief openbaar maken van uw documenten routinematig werk is (veel dezelfde soorten documenten of veel documenten)? In dit geval raden wij u aan om dit proces te automatiseren door middel van een systeemkoppeling met de aanlever-API. Is dit niet het geval, raden wij u aan om documenten handmatig actief openbaar te maken door gebruik te maken van het aanleverloket.
          </p>
          <HeadingH3Atom>Veel gestelde vragen</HeadingH3Atom>
          <p>
          Op <a target={"_blank"} href="https://www.koopoverheid.nl/voor-overheden/rijksoverheid/plooi-platform-open-overheidsinformatie/veelgestelde-vragen-plooi">Veel gestelde vragen PLOOI</a> vindt u een antwoord op veel gestelde vragen.
          </p>
          <HeadingH3Atom>Op de hoogte blijven?</HeadingH3Atom>
          <p>
          <a target={"_blank"} href="SSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSSS">Meld u aan</a> voor de nieuwsbrief om op de hoogte te blijven van de laatste ontwikkelingen.
          </p>
          <HeadingH3Atom>Hulp & Contact</HeadingH3Atom>
          <p>
          Meer uitleg over het aanleverloket staat beschreven in de <a target={"_blank"} href="https://www.koopoverheid.nl/voor-overheden/rijksoverheid/plooi-platform-open-overheidsinformatie">handleiding</a>. Op <a target={"_blank"} href="https://www.koopoverheid.nl/voor-overheden/rijksoverheid/plooi-platform-open-overheidsinformatie">de website van KOOP</a> vindt u veel informatie over de laatste ontwikkelingen omtrent PLOOI.
          </p>
          <p>
          Heeft u verder nog vragen? Neem dan contact op met het PLOOI team door een e-mail te sturen naar plooi@koop.overheid.nl.
          </p>
        </div>
      }
    />
  );
}
