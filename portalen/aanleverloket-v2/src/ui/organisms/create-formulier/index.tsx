import { useState } from "react";
import { Identifier } from "../../../models";
import { CheckboxAtom, ContainerAtom, HeadingH2Atom, SpacerAtom } from "../../atoms";
import {
  FileInputMolecule,
  SelectMultiMolecule,
  TextInputMolecule,
  FormMolecule,
  FoldableMolecule,
  LabelMolecule,
  IconButtonMolecule,
  DateInputMolecule,
  SpinnerPopupMolecule,
  TooltipMolecule,
  TimeInputMolecule
}
  from "../../molecules";

import { useAppDispatch, useAppSelector } from "../../../store/hooks";
import { updateForm } from "../../../store/reducers/metadata";
import { CategoryList } from "../../molecules/select-multi";
import { RequiredFileValidator, RequiredListValidator, RequiredValidator } from "../../../validations/requiredValidator";
import { MaxFileSizeValidator } from "../../../validations/maxSizeValidator";
import { MinimumLengthValidator } from "../../../validations/minimumLengthValidator";
import { formatDate, stringToDate } from "../../../utils/DateFormatter";
import { WaardelijstFactory, DropdownInfoFactory } from "../../../factories";
import { IdentifierWithChildren } from "../../../factories/waardelijst";
import { Documenthandelingen } from "../../../models/metadata";


const defaultNewHandeling = { soortHandeling: { id: "" }, atTime: new Date().toJSON(), wasAssociatedWith: { id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034" } };

// Handelingen
// Geldigheid

export function CreateFormulierOrganism() {
  const fetching = useAppSelector(s => s.meta.fetching);
  const formState = useAppSelector(s => s.meta.data[s.meta.formIndex]);
  const authState = useAppSelector(s => s.auth);
  const dispatch = useAppDispatch();

  const {
    classificatiecollectie,
    documenthandelingen,
    identifiers,
    language,
    titelcollectie,
    aggregatiekenmerk,
    creatiedatum,
    geldigheid,
    naamOpsteller,
    omschrijvingen,
    onderwerpen,
    verantwoordelijke,
    publisher
  } = formState.meta.document;

  const [forRijksoverheid, setForRijksoverheid] = useState<boolean>(false);
  const [uitgesteldPubliceren, setUitgesteldPubliceren] = useState<boolean>(false);
  const [DateUitgesteldPubliceren, setDateUitgesteldPubliceren] = useState<Date | undefined>();
  const document = formState.file;

  const wlFactory = new WaardelijstFactory();
  const dropdownFactory = new DropdownInfoFactory();

  const setDocument = (f: File | undefined) => {
    dispatch(updateForm({
      ...formState,
      file: f
    }));
  }

  const updateDocumenthandelingenOrganisatie = (index: number, value: Identifier) => {
    const _handelingen = [...documenthandelingen];
    const handeling = _handelingen[index];

    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          documenthandelingen: [
            ..._handelingen
              .filter(_h => _h !== handeling)
              .concat({
                ...handeling,
                wasAssociatedWith: value
              })
          ]
        }
      }
    }));
  }

  const updateDocumenthandelingenSoort = (index: number, value: Identifier) => {
    const _handelingen = [...documenthandelingen];
    const handeling = _handelingen[index];

    dispatch(updateForm({
      ...formState,
      meta: {

        document: {
          ...formState.meta.document,
          documenthandelingen: [
            ..._handelingen
              .filter(_h => _h !== handeling)
              .concat({
                ...handeling,
                soortHandeling: value
              })
          ]
        }
      }
    }));
  }

  const updateDocumenthandelingenDate = (index: number, value: string) => {
    if (!value || !value.length)
      return;

    const _handelingen = [...documenthandelingen];
    const handeling = _handelingen[index];

    debugger;
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          documenthandelingen: [
            ..._handelingen
              .filter(_h => _h !== handeling)
              .concat({
                ...handeling,
                atTime: new Date(value).toJSON()
              })
          ]
        }
      }
    }));
  }

  const removeDocumenthandelingenSoort = (index: number) => {
    const _handelingen = [...documenthandelingen];
    _handelingen.splice(index, 1);

    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          documenthandelingen: _handelingen
        }
      }
    }));
  }

  const updateVerantwoordelijke = (verantwoordelijke: Identifier) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          verantwoordelijke
        }
      }
    }));
  }

  const updateOmschrijving = (_omschrijvingen: string) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          omschrijvingen: [_omschrijvingen]
        }
      }
    }));
  }

  const updateDocumentsoorten = (documentsoorten: Identifier[]) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          classificatiecollectie: {
            ...formState.meta.document.classificatiecollectie,
            documentsoorten
          }
        }
      }
    }));
  }

  const updateThemas = (themas: Identifier[]) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          classificatiecollectie: {
            ...formState.meta.document.classificatiecollectie,
            themas
          }
        }
      }
    }));
  }

  const updateDocumentHandelingen = (_handelingen: Documenthandelingen[]) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          documenthandelingen: _handelingen
        }
      }
    }));
  }

  const updateIdentifiers = (identifiers: string[]) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          identifiers
        }
      }
    }));
  }

  const updateOnderwerpen = (onderwerpen: string[]) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          onderwerpen
        }
      }
    }));
  }

  const updateVerkorteTitel = (verkorteTitel: string) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          titelcollectie: {
            ...formState.meta.document.titelcollectie,
            verkorteTitels: [verkorteTitel]
          }
        }
      }
    }));
  }

  const updateAltTitel = (altTitel: string) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          titelcollectie: {
            ...formState.meta.document.titelcollectie,
            alternatieveTitels: [altTitel]
          }
        }
      }
    }));
  }

  const updateTitel = (titel: string) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          titelcollectie: {
            ...formState.meta.document.titelcollectie,
            officieleTitel: titel
          }
        }
      }
    }));
  }

  const updatePublisher = (publisher: Identifier) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          publisher
        }
      }
    }));
  }

  const updateOpsteller = (naamOpsteller: string) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          naamOpsteller
        }
      }
    }));
  }

  const updateTaal = (language: Identifier) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          language
        }
      }
    }));
  }

  const updateCreatiedatum = (creatiedatum: string) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          creatiedatum
        }
      }
    }));
  }

  const updateGeldigheidBegindatum = (begindatum: Date | undefined) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          geldigheid: {
            ...formState.meta.document.geldigheid,
            begindatum
          }
        }
      }
    }));
  }

  const updateGeldigheidEinddatum = (einddatum: Date | undefined) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          geldigheid: {
            ...formState.meta.document.geldigheid,
            einddatum
          }
        }
      }
    }));
  }

  const updateAggegratieKenmerk = (kenmerk: string) => {
    dispatch(updateForm({
      ...formState,
      meta: {
        document: {
          ...formState.meta.document,
          aggregatiekenmerk: kenmerk
        }
      }
    }));
  }

  const item = { children: wlFactory.createMinisterieLijst() } as IdentifierWithChildren;

  const associatedWithOptions = function (index: number): CategoryList[] {
    const results = dropdownFactory.generateCategoryListMultiArray(index, item, [documenthandelingen[index].wasAssociatedWith], updateDocumenthandelingenOrganisatie);
    results.options.map(r => r.id = `associatedWith_${r.id}`);
    return [results];
  };

  const documenthandelingenOptions = function (index: number): CategoryList[] { return wlFactory.createDocumenthandelingenLijst().map((item) => dropdownFactory.generateCategoryListMultiArray(index, item, [documenthandelingen[index].soortHandeling], updateDocumenthandelingenSoort)); };

  const docTypeOptions = wlFactory.createDocumentsoortenLijst().map(item => dropdownFactory.generateCategoryListMultiSelect(item, classificatiecollectie.documentsoorten, updateDocumentsoorten));
  const themaLijst = wlFactory.createThemaLijst().map(item => dropdownFactory.generateCategoryListMultiSelect(item, classificatiecollectie.themas, updateThemas));

  const publisherOptions = dropdownFactory.generateCheckboxListSingleSelect(authState.organizations, publisher, updatePublisher, formState.meta.document.publisher).map(r => {
    r.id = `publicerende_organisatie_${r.id}`;
    return r;
  });


  const taalOptions = dropdownFactory.generateCheckboxListSingleSelect(wlFactory.createTaalLijst(), language, updateTaal, { id: "http://publications.europa.eu/resource/authority/language/NLD", label: "Nederlands" });
  const verantwoordelijkeOptions = dropdownFactory.generateCheckboxListSingleSelect(authState.organizations, verantwoordelijke, updateVerantwoordelijke, formState.meta.document.publisher);

  const inputLengthValidations = [new MinimumLengthValidator(3)];
  const inputValidations = [new RequiredValidator()];
  const listValidations = [new RequiredListValidator()];
  const fileInputValidations = [new MaxFileSizeValidator(2000000000), new RequiredFileValidator()];
  const requiredRefs: HTMLInputElement[] = [];

  const verkorteTitel = titelcollectie.verkorteTitels && titelcollectie.verkorteTitels.length > 0 ? titelcollectie.verkorteTitels[0] : "";
  const altTitel = titelcollectie.alternatieveTitels && titelcollectie.alternatieveTitels.length > 0 ? titelcollectie.alternatieveTitels[0] : "";

  return (
    <>
      <FormMolecule
      // leftButton={<a href="#/documenten"><ButtonMolecule id={"annuleer-button"} onClick={() => { }} text={"Annuleren"} type={"default"} /></a>}
      // rightButton={<ButtonMolecule id={"upload-button"} onClick={onSubmit} text={"Publiceren"} type={"primary"} title={"Submit"} />}
      >
        <HeadingH2Atom>Document uploaden</HeadingH2Atom>
        <FileInputMolecule inputRef={(r: any) => requiredRefs.push(r)} validations={fileInputValidations} key={"file"} selectedFileName={document?.name} onSelectFile={setDocument} required></FileInputMolecule>
        <SpacerAtom space={4} />
        <SelectMultiMolecule key={"publicerende"} id={"publicerende_organisatie"} label={"Publicerende organisatie"} placeholder={"Publicerende organisatie"} categories={[{ options: publisherOptions }]} tooltip={"Vul hier de overheidsorganisatie in die de wettelijke verantwoordelijkheid draagt voor de actieve openbaarmaking van het document."} singleSelect required validations={listValidations} />
        <SpacerAtom space={4} />
        <TextInputMolecule inputRef={r => requiredRefs.push(r)} key={"titel"} id={"titel"} label={"Titel"} placeholder={"Voer titel in"} onChange={updateTitel}
          value={titelcollectie.officieleTitel} onEnter={() => { }} tooltip={["Dit is de titel die op open.overheid.nl wordt getoond bij het document.", "Deze titel is bij voorkeur uniek binnen het bestuursorgaan.", "Daarnaast beschrijft de titel het document op een manier die duidelijk is voor de gebruiker van open.overheid.nl."]} required validations={inputValidations} />
        <SpacerAtom space={4} />
        <SelectMultiMolecule inputRef={r => {
          if (classificatiecollectie.documentsoorten.length === 0)
            requiredRefs.push(r);
        }} key={"documentsoort"} id={"documentsoort"} label={"Documentsoort"} placeholder={"Voer documentsoort in"} categories={docTypeOptions} tooltip={["Om zoekresultaten te kunnen filteren en gebruikers snel inzicht te geven in de aard van het document, moet een documentsoort worden meegegeven.", "Eén document kan tot meerdere documentsoorten behoren, al zal dat niet vaak voorkomen."]} required validations={listValidations} />
        <SpacerAtom space={4} />
        <TextInputMolecule key={"omschrijving"} id={"omschrijving"} label={"Omschrijving document"} placeholder={"Voer omschrijving in"} value={omschrijvingen && omschrijvingen?.length > 0 ? omschrijvingen[0] : ""} onChange={updateOmschrijving} onEnter={() => { }} tooltip={"Beschrijf hier in enkele zinnen waar het document over gaat.  De omschrijving is uitgebreider dan het onderwerp. Het vullen van dit veld verbetert de vindbaarheid van het document op open.overheid.nl."} validations={inputLengthValidations} />
        <SpacerAtom space={4} />
        <TextInputMolecule inputRef={r => requiredRefs.push(r)} key={"identifier"} id={"identifier"} label={"Identifier"} placeholder={"Voer identifier in"} onChange={(value: string) => { updateIdentifiers([value]) }} value={identifiers[0]} tooltip={"Vul hier de identifier van het document in. Deze wordt door de aanleverende organisatie zelf aan het document toegekend. Binnen de aanleverende organisatie is deze identifier uniek."} required validations={inputValidations} />
        <SpacerAtom space={4} />
        <LabelMolecule label={"Documentdatum"} tooltip={["De Documentdatum geeft aan op welke datum een officiële handeling met betrekking tot het document heeft plaatsgevonden.", "Documenthandelingen zijn samengesteld uit een datum, een handeling en een voor de handeling verantwoordelijke organisatie.", "Er moet ten minste één documenthandeling worden ingevuld."]} required />
        {
          documenthandelingen.map((m, index) => {
            return <div style={{ position: 'relative' }}>
              <ContainerAtom type="row">
                {/* <TextInputMolecule required validations={inputValidations} inputRef={r => requiredRefs.push(r)} key={"wasAssociatedWith" + index} id={"wasAssociatedWith" + index} placeholder={"Organisatie"} onChange={(value) => { updateDocumenthandelingenOrganisatie(index, { id: value, label: value }) }} value={documenthandeling[index].wasAssociatedWith.id} /> */}
                <SelectMultiMolecule validations={listValidations} inputRef={r => {
                  if (documenthandelingen[index].wasAssociatedWith.id === "")
                    requiredRefs.push(r);
                }} key={"wasAssociatedWith" + index} id={"wasAssociatedWith" + index} placeholder={"Organisatie"} label={""} categories={associatedWithOptions(index)} singleSelect search={false} />
                <SelectMultiMolecule validations={listValidations} inputRef={r => {
                  if (documenthandelingen[index].soortHandeling.id === "")
                    requiredRefs.push(r);
                }} key={"soortHandeling" + index} id={"soortHandeling" + index} placeholder={"Handeling"} label={""} categories={documenthandelingenOptions(index)} singleSelect search={false} />
                <DateInputMolecule required validations={inputValidations} inputRef={r => requiredRefs.push(r)} key={"atTime" + index} id={"atTime" + index} onChange={(value) => { updateDocumenthandelingenDate(index, value) }} value={formatDate(stringToDate(documenthandelingen[index].atTime))} />
                {
                  documenthandelingen.length > 1 &&
                  <div style={{ position: 'absolute', right: '-75px' }}>
                    <IconButtonMolecule id={"remove-handeling" + index} title={"Verwijder handeling"} icon={"icon-remove-blue"} type={"default"} size={"inputfieldSize"} onClick={() => { removeDocumenthandelingenSoort(index) }} />
                  </div>
                }


              </ContainerAtom>
              {
                index != documenthandelingen.length - 1 && <SpacerAtom space={4} />
              }
            </div>
          })
        }
        <SpacerAtom space={4} />
        <div style={{ display: 'flex', justifyContent: 'end', alignItems: 'center' }}>
          <IconButtonMolecule text="Voeg handeling toe" title="Voeg handeling toe" icon={"icon-plus-shape"} id={"add-documenthandeling-button"} onClick={() => { updateDocumentHandelingen([...documenthandelingen, defaultNewHandeling]) }} type={"default"} size={"medium"} />
        </div>

        <SpacerAtom space={4} />
        <SelectMultiMolecule inputRef={r => {
          if (classificatiecollectie.themas.length === 0)
            requiredRefs.push(r);
        }} key={"thema"} id={"thema"} label={"Thema's"} placeholder={"Kies thema('s)"} categories={themaLijst} tooltip={"Om documenten te kunnen ordenen naar een globaal onderwerp wordt gebruik gemaakt van een themalijst. Een document kan onder meerdere thema’s vallen."} required validations={listValidations} />
        <SpacerAtom space={4} />
        <SelectMultiMolecule key={"taal"} id={"taal"} label={"Taal"} placeholder={"Taal"} categories={[{ options: taalOptions }]} tooltip={"Vul hier de taal in waarin het document is geschreven."} singleSelect required validations={listValidations} />
        <SpacerAtom space={4} />
        <div style={{ display: "flex", alignItems: "center" }}>
          <CheckboxAtom id={"rijksoverheid.nl"} checked={forRijksoverheid} onClick={() => { setForRijksoverheid(!forRijksoverheid) }} label={"Bestemd voor Rijksoverheid.nl"} />
          <TooltipMolecule id={"Rijksoverheid.nl-tooltip"} content={"test"} />
        </div>
        <SpacerAtom space={4} />
        <div style={{ display: "flex", alignItems: "center" }}>
          <CheckboxAtom id={"uitgesteld"} checked={uitgesteldPubliceren} onClick={() => { setUitgesteldPubliceren(!uitgesteldPubliceren); setDateUitgesteldPubliceren(new Date()) }} label={"Uitgesteld publiceren"} />
          <TooltipMolecule id={"Uitgesteld publiceren-tooltip"} content={"test"} />
        </div>
        <SpacerAtom space={4} />
        {
          uitgesteldPubliceren && <ContainerAtom type="row">
            <DateInputMolecule key={"uitsteldatum"} id={"uitsteldatum"} placeholder={"Kies datum"} value={DateUitgesteldPubliceren ? formatDate(DateUitgesteldPubliceren) : undefined} onChange={(value) => {
              if (value.length === 0) {
                setDateUitgesteldPubliceren(undefined);
                return;
              }
              const g = DateUitgesteldPubliceren ? new Date(DateUitgesteldPubliceren) : new Date();

              let newFromDate = stringToDate(value);
              if (newFromDate < new Date)
                newFromDate = new Date;

              g?.setFullYear(newFromDate.getFullYear());
              g?.setMonth(newFromDate.getMonth());
              g?.setDate(newFromDate.getDate());

              setDateUitgesteldPubliceren(g);
            }} onEnter={() => { }} />
            <TimeInputMolecule minDate={geldigheid?.begindatum} key={"uitsteltijd"} id={"uitsteltijd"} placeholder={"Kies tijd"} value={(DateUitgesteldPubliceren?.getHours()! < 10 ? '0' : '') + DateUitgesteldPubliceren?.getHours() + ':' + (DateUitgesteldPubliceren?.getMinutes()! < 10 ? '0' : '') + DateUitgesteldPubliceren?.getMinutes()} onChange={(value) => {
              if (value.length === 0)
                return;

              let g = DateUitgesteldPubliceren ? new Date(DateUitgesteldPubliceren) : new Date();
              const s = value.split(":");
              g?.setHours(Number(s[0]));
              g?.setMinutes(Number(s[1]));

              if (g < new Date)
                g = new Date;

              setDateUitgesteldPubliceren(g);
            }} onEnter={() => { }} />
          </ContainerAtom> || !uitgesteldPubliceren && <SpacerAtom space={"58px"} />
        }
        <SpacerAtom space={4} />

        <FoldableMolecule label={"Optionele velden"} defaultFolded>
          <SpacerAtom space={4} />
          <SelectMultiMolecule key={"verantwoordelijk"} id={"verantwoordelijke organisatie"} label={"Verantwoordelijke organisatie"} placeholder={"Verantwoordelijke organisatie"} categories={[{ options: verantwoordelijkeOptions }]} tooltip={"Vul hier de overheidsorganisatie in die de wettelijke verantwoordelijkheid draagt voor de actieve openbaarmaking van het document."} singleSelect />
          <SpacerAtom space={4} />
          <TextInputMolecule key={"onderwerp"} id={"onderwerp"} label={"Onderwerp"} placeholder={"Voer onderwerp in"} value={onderwerpen && onderwerpen.length > 0 ? onderwerpen[0] : ""} onChange={v => updateOnderwerpen([v])} onEnter={() => { }} tooltip={["Beschrijf hier in één of enkele woorden waar het document over gaat.", "Het vullen van dit veld verbetert de vindbaarheid van het document op open.overheid.nl."]} validations={inputLengthValidations} />
          <SpacerAtom space={4} />
          <TextInputMolecule key={"verkorte-titel"} id={"verkorte-titel"} label={"Verkorte titel"} placeholder={"Voer verkorte titel in"} value={verkorteTitel} onChange={updateVerkorteTitel} onEnter={() => { }} tooltip={"Geef hier een verkorte aanduiding van een mogelijk veel langere (officiële) titel."} validations={inputLengthValidations} />
          <SpacerAtom space={4} />
          <TextInputMolecule key={"alternatieve-titel"} id={"alternatieve-titel"} label={"Alternatieve titel"} placeholder={"Voer alternatieve titel in"} value={altTitel} onChange={updateAltTitel} onEnter={() => { }} tooltip={["Geef het document een alternatieve titel. Zorg ervoor dat deze anders is dan de officiële of verkorte titel.", "Het vullen van dit veld verbetert de vindbaarheid van het document op open.overheid.nl."]} validations={inputLengthValidations} />
          <SpacerAtom space={4} />
          <TextInputMolecule key={"opsteller"} id={"opsteller"} label={"Opsteller"} placeholder={"Voer opsteller in"} value={naamOpsteller} onChange={updateOpsteller} onEnter={() => { }} tooltip={["Vul hier de organisatie in die het document initieel heeft gemaakt. Dit hoeft geen overheidsorganisatie te zijn, maar kan bijvoorbeeld ook een adviesbureau zijn.", "Als deze leeg is, dan is deze gelijk aan de openbaarmakende organisatie."]} />
          <SpacerAtom space={4} />
          <TextInputMolecule key={"creatiedatum"} id={"creatiedatum"} label={"Creatiedatum"} placeholder={"Kies creatiedatum"} value={creatiedatum} onChange={updateCreatiedatum} onEnter={() => { }} tooltip={"Vul hier de datum in waarop het document initieel is gemaakt. Deze datum kan op de dag nauwkeurig zijn, maar ook een maand of jaar."} />
          <SpacerAtom space={4} />
          <LabelMolecule label={"Geldigheid"} required={!!geldigheid?.begindatum || !!geldigheid?.einddatum} tooltip={["Vul hier de periode in waarbinnen het document geldig is.", "De geldigheid kan gebruikt worden om aan te geven dat een document niet meer geldig is, bijvoorbeeld omdat het door een ander document is vervangen. Het oude document hoeft dan niet worden verwijderd, maar het is wel duidelijk dat het niet meer geldig is."]} />
          <ContainerAtom type="row">
            <DateInputMolecule key={"begindatum"} id={"begindatum"} placeholder={"Kies begindatum"} value={geldigheid?.begindatum?.getDate ? formatDate(geldigheid?.begindatum) : undefined} onChange={(value) => {
              if (value.length === 0) {
                updateGeldigheidBegindatum(undefined);
                return;
              }

              const newFromDate = stringToDate(value);

              //Todo
              // if (geldigheid?.einddatum && newFromDate >= geldigheid.einddatum)
              //   updateGeldigheidEinddatum(newFromDate);

              updateGeldigheidBegindatum(newFromDate);
            }} onEnter={() => { }} />
            <DateInputMolecule minDate={geldigheid?.begindatum} key={"einddatum"} id={"einddatum"} placeholder={"Kies einddatum"} value={geldigheid?.einddatum?.getTime ? formatDate(geldigheid?.einddatum) : undefined} onChange={(value) => { updateGeldigheidEinddatum(value.length ? stringToDate(value) : undefined) }} onEnter={() => { }} />
          </ContainerAtom>
          <SpacerAtom space={4} />
          <TextInputMolecule key={"aggregatiekenmerk"} id={"aggregatiekenmerk"} label={"Aggregatiekenmerk"} placeholder={"Voer aggregatiekenmerk in"} value={aggregatiekenmerk} onChange={updateAggegratieKenmerk} onEnter={() => { }} tooltip={"Vul hier de door aanleverende organisatie aangedragen tekst in, die een aantal documenten met elkaar gemeen kunnen hebben."} />
        </FoldableMolecule>

      </FormMolecule>

      {
        fetching && <SpinnerPopupMolecule />
      }

    </>
  );
}
