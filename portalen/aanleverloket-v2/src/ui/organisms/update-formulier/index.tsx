import { useState } from "react";
import { Identifier } from "../../../models";
import { ContainerAtom, HeadingH2Atom, SpacerAtom } from "../../atoms";
import { CheckboxFactory, MetadataFactory } from '../../../factories';
import {
  SelectMultiMolecule,
  TextInputMolecule,
  FormMolecule,
  FoldableMolecule,
  LabelMolecule,
  DateInputMolecule,
  IconButtonMolecule,
  FileInputMolecule,
  SpinnerPopupMolecule,
  ButtonMolecule
} from "../../molecules";
import { StringSanitizer } from "../../../utils/StringSanitizer";
import { useAppDispatch, useAppSelector } from "../../../store/hooks";
import { FormState, replaceDocument, updateMetadata } from "../../../store/reducers/metadata";
import { selectMetadata } from "../../../store/selectors";
import { IdentifierWithChildren, WaardelijstFactory, WaardelijstItem } from "../../../factories/waardelijst";
import { CategoryList } from "../../molecules/select-multi";
import { setToast } from "../../../store/reducers/toast";
import { MaxFileSizeValidator } from "../../../validations/maxSizeValidator";
import { RequiredValidator, RequiredListValidator, RequiredFileValidator } from "../../../validations/requiredValidator";
import { MinimumLengthValidator } from "../../../validations/minimumLengthValidator";
import { DropdownInfoFactory } from '../../../factories';
import { Documenthandelingen } from "../../../models/metadata";

interface Props {
  data: FormState[];
}

export function UpdateFormulierOrganism(props: Props) {
  const dispatch = useAppDispatch();
  const { fetching } = useAppSelector(selectMetadata);
  
  const sanitizer = new StringSanitizer();
  const checkboxFactory = new CheckboxFactory();
  const dropdownFactory = new DropdownInfoFactory();

  const status = "Gepubliceerd";
  const [aanleverdatum] = useState<Date | undefined>(props.data[0].meta.document.openbaarmakingsdatum ?? undefined);
  const [wijzigingsdatum] = useState<Date | undefined>(props.data[0].meta.document.wijzigingsdatum ?? undefined);
  const [publisher] = useState<Identifier>(props.data[0].meta.document.publisher ?? { id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034", label: "ministerie van Binnenlandse Zaken en Koninkrijksrelaties" });
  const [verantwoordelijke] = useState<Identifier>(props.data[0].meta.document.verantwoordelijke ?? { id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034", label: "ministerie van Binnenlandse Zaken en Koninkrijksrelaties" });
  const [identifier, setIdentifier] = useState(props.data[0].meta.document.identifiers);
  const [documenthandeling, setDocumenthandelingen] = useState<Documenthandelingen[]>(props.data[0].meta.document.documenthandelingen);

  const [onderwerp, setOnderwerp] = useState(props.data[0].meta.document.onderwerpen?.join(" ") ?? undefined);
  const [omschrijving, setOmschrijving] = useState(props.data[0].meta.document.omschrijvingen?.join(" ") ?? undefined);
  const [titel, setTitel] = useState(props.data[0].meta.document.titelcollectie?.officieleTitel);
  const [shortTitle, setShortTitle] = useState(props.data[0]?.meta.document.titelcollectie?.verkorteTitels?.join(" ") ?? undefined);
  const [alternativeTitle, setAlternativeTitle] = useState(props.data[0].meta.document.titelcollectie?.alternatieveTitels?.join(" ") ?? undefined);

  const [selectedThemas, setSelectedThemas] = useState<Identifier[]>(props.data[0].meta.document.classificatiecollectie?.themas);
  const [docTypes, setDocTypes] = useState<Identifier[]>(props.data[0].meta.document.classificatiecollectie?.documentsoorten);
  const [taal, setTaal] = useState<Identifier>(props.data[0].meta.document.language);
  const [opsteller, setOpsteller] = useState<string | undefined>(props.data[0].meta.document.naamOpsteller ?? undefined);
  const [creatiedatum, setCreatiedatum] = useState<string | undefined>(props.data[0].meta.document.creatiedatum ?? undefined);
  const [aggregatiekenmerk, setAggregatiekenmerk] = useState<string | undefined>(props.data[0].meta.document.aggregatiekenmerk ?? undefined);
  const [geldigheidBegindatum, setGeldigheidBegindatum] = useState<Date | undefined>(props.data[0].meta.document.geldigheid?.begindatum ?? undefined);
  const [geldigheidEinddatum, setGeldigheidEinddatum] = useState<Date | undefined>(props.data[0].meta.document.geldigheid?.einddatum ?? undefined);
  const [pid] = useState<string | undefined>(props.data[0].meta.document.pid ?? undefined);
  const [document, setDocument] = useState<File>();

  const generateCategoryList = function (item: WaardelijstItem): CategoryList {
    let categoryList: CategoryList = ({
      title: item.item.label,
      options: !item.children ? [] : item.children.map(ds => checkboxFactory.create(
        sanitizer.sanitizeWaardelijstItem(ds.item),
        docTypes.some(docTypes => docTypes.id === ds.item.id),
        () => onWaardelijstItemClicked(ds.item, docTypes, setDocTypes),
        false,
        false
      )),
      sublist: item.children?.filter(f => f.children).map(t => generateCategoryList(t))
    });

    return categoryList;
  }

  const generateCategoryListDocumentHandelingen = function (index: number, item: IdentifierWithChildren, selectedValue: Identifier, setter: (index: number, values: Identifier) => void): CategoryList {
    let list: CategoryList = ({
      title: item.label,
      options: !item.children ? [] : item.children.map(child => checkboxFactory.create(
        sanitizer.sanitizeWaardelijstItem(child),
        child.id === documenthandeling[index].soortHandeling.id,
        () => setter(index, child),
        false,
        false
      )),
      sublist: item.children?.filter(f => f.children).map(t => generateCategoryListDocumentHandelingen(index, t, selectedValue, setter)),
    });

    return list;
  }


  const taalOptions = dropdownFactory.generateCheckboxListSingleSelect(new WaardelijstFactory().createTaalLijst(), taal, setTaal, { id: "http://publications.europa.eu/resource/authority/language/NLD", label: "Nederlands" });
  const documentsoortenLijst = new WaardelijstFactory().createDocumentsoortenLijst();
  const docTypeOptions = new WaardelijstFactory().createDocumentsoortenLijst().map(item => dropdownFactory.generateCategoryListMultiSelect(item, docTypes, setDocTypes));
  const documenthandelingenLijst = new WaardelijstFactory().createDocumenthandelingenLijst();
  const documenthandelingenOptions = function (index: number): CategoryList[] { return documenthandelingenLijst.map((item) => generateCategoryListDocumentHandelingen(index, item, item, updateDocumenthandelingenSoort)); }

  const themaLijst = new WaardelijstFactory().createThemaLijst().map(item => ({
    title: item.label,
    options: !item.children ? [] : item.children.map(thema => checkboxFactory.create(
      thema,
      selectedThemas.some(selectedThema => selectedThema.id === thema.id),
      () => onWaardelijstItemClicked(thema, selectedThemas, setSelectedThemas),
      false,
      false
    ))
  }));

  const onWaardelijstItemClicked = (item: Identifier, selectedValues: Identifier[], setter: (values: Identifier[]) => void) => {
    if (selectedValues.some(v => v.id === item.id)) {
      setter([...selectedValues.filter(v => v.id !== item.id)]);
      return;
    }

    setter([...selectedValues, item]);
  }

  function deepEqual(object1: any, object2: any) {
    const keys1 = Object.keys(object1);
    // const keys2 = Object.keys(object2);

    for (const key of keys1) {
      const val1 = object1[key];
      const val2 = object2[key];
      const areObjects = isObject(val1) && isObject(val2);

      if (
        areObjects && !deepEqual(val1, val2) ||
        !areObjects && val1 !== val2
      ) {
        return false;
      }
    }
    return true;
  }

  function isObject(object: any) {
    return object != null && typeof object === 'object';
  }

  const onSubmit = () => {
    if (validateForm()) {
      const metadata = new MetadataFactory().create(
        publisher,
        identifier,
        taal,
        {
          officieleTitel: titel,
          alternatieveTitels: alternativeTitle && alternativeTitle.length > 0 ? [alternativeTitle] : undefined,
          verkorteTitels: shortTitle && shortTitle.length > 0 ? [shortTitle] : undefined
        },
        {
          documentsoorten: docTypes,
          themas: selectedThemas,
        },
        documenthandeling,

        creatiedatum,
        verantwoordelijke,
        opsteller,
        omschrijving ? [omschrijving] : undefined,
        document ? document?.name && document?.name.indexOf('pdf') > -1 ? { id: "http://publications.europa.eu/resource/authority/file-type/PDF", label: 'PDF' } : { id: "http://publications.europa.eu/resource/authority/file-type/ZIP", label: 'ZIP' } : props.data[0].meta.document.format,
        onderwerp ? [onderwerp] : undefined,
        aggregatiekenmerk,
        undefined,
        !geldigheidBegindatum && !geldigheidEinddatum ? undefined : { begindatum: geldigheidBegindatum, einddatum: geldigheidEinddatum },
        pid
      );

      let metadataSame = deepEqual(metadata, props.data);

      if (!metadataSame)
        dispatch(updateMetadata(metadata)).then((result) => {
          if (result.meta.requestStatus === "fulfilled" && document && pid) {
            dispatch(replaceDocument({ pid: pid, file: document }));
          }
        });
      else if (document && pid) {
        dispatch(replaceDocument({ pid: pid, file: document }));
      }

    }
    else {
      for (let i = 0; i < requiredRefs.length; i++) {
        const element = requiredRefs[i];
        if (!element.value || element.value.length === 0) {
          element.focus();
          element.blur();
          return;
        }
      }

      dispatch(setToast({
        autoClose: true,
        type: 'error',
        message: {
          message: 'Niet alle benodigde velden zijn ingevuld.'
        }
      }));
    }
  };

  const validateForm = (): boolean => {
    const requiredValues = [
      identifier,
      taal?.id,
      titel,
      docTypes,
      selectedThemas,
      document?.name
    ];

    if (geldigheidBegindatum || geldigheidEinddatum) {
      requiredValues.push(geldigheidEinddatum ? formatDate(geldigheidEinddatum) : "");
      requiredValues.push(geldigheidBegindatum ? formatDate(geldigheidBegindatum) : "");
    }

    return requiredValues.every(v => v && v.length > 0);
  };

  const stringToDate = (value: string) => {
    const pattern = /(\d{2})\.(\d{2})\.(\d{4})/;
    return new Date(value.replace(pattern, '$3-$2-$1'));
  }

  const updateDocumenthandelingenDate = (index: number, value: string) => {
    const dt = value;
    setDocumenthandelingen(documenthandeling.map((v, i: number) => {
      if (i === index) {
        return { ...v, atTime: dt };
      } else {
        return v;
      }
    }));
  }

  const updateDocumenthandelingenOrganisatie = (index: number, value: Identifier) => {
    setDocumenthandelingen(documenthandeling.map((v, i: number) => {
      if (i === index) {
        return { ...v, wasAssociatedWith: value };
      } else {
        return v;
      }
    }));
  }

  const updateDocumenthandelingenSoort = (index: number, value: Identifier) => {
    setDocumenthandelingen(documenthandeling.map((v, i: number) => {
      if (i === index) {
        return { ...v, soortHandeling: value };
      } else {
        return v;
      }
    }));
  }

  const removeDocumenthandelingenSoort = (index: number) => {
    let newArr = [...documenthandeling];
    newArr.splice(index, 1);
    setDocumenthandelingen(newArr);
  }

  function padTo2Digits(num: number) {
    return num.toString().padStart(2, '0');
  }

  function formatDate(date = new Date()) {
    return [
      date.getFullYear(),
      padTo2Digits(date.getMonth() + 1),
      padTo2Digits(date.getDate()),
    ].join('-');
  }

  const inputLengthValidations = [new MinimumLengthValidator(3)];
  const inputValidations = [new RequiredValidator()];
  const listValidations = [new RequiredListValidator()];
  const fileInputValidations = [new MaxFileSizeValidator(2000000000), new RequiredFileValidator()];

  const requiredRefs: HTMLInputElement[] = [];

  return (
    <>
      <FormMolecule
      leftButton={<a href="#/documenten"><ButtonMolecule id={"annuleer-button"} onClick={() => { } } text={"Annuleren"} type={"default"} title={""} /></a>}
      rightButton={<ButtonMolecule id={"upload-button"} onClick={onSubmit} text={"Opslaan"} type={"primary"} title={""} />}
      >
        <HeadingH2Atom>Gegevens bewerken</HeadingH2Atom>
        <SpacerAtom space={8} />
        <ContainerAtom type="row">
          <TextInputMolecule key={"documentstatus"} id={"documentstatus"} label={"Documentstatus"} placeholder={""} onChange={() => { }} value={status} disabled />
          <DateInputMolecule key={"aanleverdatum"} id={"aanleverdatum"} label={"Aanleverdatum"} onChange={() => { }} value={formatDate(aanleverdatum)} tooltip={"Dit is de datum waarop het document is gepubliceerd. "} disabled />
          <DateInputMolecule key={"bewerkt"} id={"bewerkt"} label={"Bewerkt"} onChange={() => { }} value={formatDate(wijzigingsdatum)} tooltip={"Dit is de datum waarop de metadata of het document zelf zijn gewijzigd."} disabled />
        </ContainerAtom>
        <SpacerAtom space={4} />
        <TextInputMolecule key={"organisatie"} id={"verantwoordelijke organisatie"} label={"Verantwoordelijke organisatie"} placeholder={"Verantwoordelijke organisatie"} onChange={() => { }} value={verantwoordelijke.label} tooltip={"Vul hier de overheidsorganisatie in die de wettelijke verantwoordelijkheid draagt voor de actieve openbaarmaking van het document."} disabled />
        <SpacerAtom space={4} />
        <TextInputMolecule inputRef={r => requiredRefs.push(r)} key={"identifier"} id={"identifier"} label={"Identifier"} placeholder={"Voer identifier in"} onChange={(value: string) => { setIdentifier([value]) }} value={identifier[0]} tooltip={"Vul hier de identifier van het document in. Deze wordt door de aanleverende organisatie zelf aan het document toegekend. Binnen de aanleverende organisatie is deze identifier uniek."} required validations={inputValidations} />
        <SpacerAtom space={4} />

        <LabelMolecule label={"Documenthandelingen"} tooltip={["De documenthandeling geeft aan op welke datum een officiële handeling met betrekking tot het document heeft plaatsgevonden.", "Documenthandelingen zijn samengesteld uit een datum, een handeling en een voor de handeling verantwoordelijke organisatie.", "Er moet ten minste één documenthandeling worden ingevuld."]} required />
        {
          documenthandeling.map((m, index) => {
            return <div style={{ position: 'relative' }}>
              <ContainerAtom type="row">
                <TextInputMolecule key={"wasAssociatedWith" + index} id={"wasAssociatedWith" + index} placeholder={"Organisatie"} onChange={(value) => { updateDocumenthandelingenOrganisatie(index, { id: value, label: value }) }} value={documenthandeling[index].wasAssociatedWith.id} />
                <SelectMultiMolecule key={"soortHandeling" + index}
                  inputRef={r => {
                    if (documenthandeling[index].soortHandeling.id === "")
                      requiredRefs.push(r);
                  }}
                  id={"soortHandeling" + index} placeholder={"Handeling"} label={""} categories={documenthandelingenOptions(index)} singleSelect search={false} />
                <DateInputMolecule key={"atTime" + index} id={"atTime" + index} onChange={(value) => { updateDocumenthandelingenDate(index, value) }} value={formatDate(stringToDate(documenthandeling[index].atTime.toString()))} />
                {
                  documenthandeling.length > 1 &&
                  <div style={{ position: 'absolute', right: '-75px' }}>
                    <IconButtonMolecule id={"remove-handeling" + index} title="Verwijder handeling" icon={"icon-remove-blue"} type={"default"} size={"inputfieldSize"} onClick={() => { removeDocumenthandelingenSoort(index) }} />
                  </div>
                }

              </ContainerAtom>
              {
                index != documenthandeling.length - 1 && <SpacerAtom space={4} />
              }
            </div>
          })
        }
        <SpacerAtom space={4} />
        <div style={{ display: 'flex', justifyContent: 'end', alignItems: 'center' }}>
          <IconButtonMolecule text="Voeg handeling toe" title="Voeg handeling toe" icon={"icon-plus-shape"} id={"add-documenthandeling-button"} onClick={() => { setDocumenthandelingen(h => [...h, { soortHandeling: { id: "" }, atTime: new Date().toJSON(), wasAssociatedWith: { id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034" } }]) }} type={"default"} size={"medium"} />
        </div>
        <SpacerAtom space={4} />

        <TextInputMolecule inputRef={r => requiredRefs.push(r)} key={"titel"} id={"titel"} label={"Officiële titel"} placeholder={"Voer titel in"} onChange={setTitel} value={titel} tooltip={["Dit is de titel die op open.overheid.nl wordt getoond bij het document.", "Deze titel is bij voorkeur uniek binnen het bestuursorgaan.", "Daarnaast beschrijft de titel het document op een manier die duidelijk is voor de gebruiker van open.overheid.nl."]} validations={inputValidations} />
        <SpacerAtom space={4} />
        <SelectMultiMolecule inputRef={r => requiredRefs.push(r)} key={"thema"} id={"thema"} label={"Thema's"} placeholder={"Kies thema('s)"} categories={themaLijst} tooltip={"Om documenten te kunnen ordenen naar een globaal onderwerp wordt gebruik gemaakt van een themalijst. Een document kan onder meerdere thema’s vallen."} required validations={listValidations} />
        <SpacerAtom space={4} />
        <SelectMultiMolecule inputRef={r => requiredRefs.push(r)} key={"documentsoort"} id={"documentsoort"} label={"Documentsoort"} placeholder={"Voer documentsoort in"} categories={docTypeOptions} tooltip={["Om zoekresultaten te kunnen filteren en gebruikers snel inzicht te geven in de aard van het document, moet een documentsoort worden meegegeven.", "Eén document kan tot meerdere documentsoorten behoren, al zal dat niet vaak voorkomen."]} required validations={listValidations} />
        <SpacerAtom space={4} />
        <SelectMultiMolecule inputRef={r => requiredRefs.push(r)} key={"taal"} id={"taal"} label={"Taal"} placeholder={"Taal"} categories={[{ options: taalOptions }]} tooltip={"Vul hier de taal in waarin het document is geschreven."} singleSelect required validations={listValidations} />
        <SpacerAtom space={4} />

        <FoldableMolecule label={"Optionele velden"} defaultFolded>
          <SpacerAtom space={4} />
          <TextInputMolecule key={"onderwerp"} id={"onderwerp"} label={"Onderwerp"} placeholder={"Voer onderwerp in"} value={onderwerp} onChange={setOnderwerp} tooltip={["Beschrijf hier in één of enkele woorden waar het document over gaat.", "Het vullen van dit veld verbetert de vindbaarheid van het document op open.overheid.nl."]} validations={inputLengthValidations} />
          <SpacerAtom space={4} />
          <TextInputMolecule key={"omschrijving"} id={"omschrijving"} label={"Omschrijving document"} placeholder={"Voer omschrijving in"} value={omschrijving} onChange={setOmschrijving} tooltip={"Beschrijf hier in enkele zinnen waar het document over gaat.  De omschrijving is uitgebreider dan het onderwerp. Het vullen van dit veld verbetert de vindbaarheid van het document op open.overheid.nl."} validations={inputLengthValidations} />
          <SpacerAtom space={4} />
          <TextInputMolecule key={"verkorte-titel"} id={"verkorte-titel"} label={"Verkorte titel"} placeholder={"Voer verkorte titel in"} value={shortTitle} onChange={setShortTitle} tooltip={"Geef hier een verkorte aanduiding van een mogelijk veel langere (officiële) titel."} validations={inputLengthValidations} />
          <SpacerAtom space={4} />
          <TextInputMolecule key={"alternatieve-titel"} id={"alternatieve-titel"} label={"Alternatieve titel"} placeholder={"Voer alternatieve titel in"} value={alternativeTitle} onChange={setAlternativeTitle} tooltip={["Geef het document een alternatieve titel. Zorg ervoor dat deze anders is dan de officiële of verkorte titel.", "Het vullen van dit veld verbetert de vindbaarheid van het document op open.overheid.nl."]} validations={inputLengthValidations} />
          <SpacerAtom space={4} />
          <TextInputMolecule key={"opsteller"} id={"opsteller"} label={"Opsteller"} placeholder={"Voer opsteller in"} value={opsteller} onChange={setOpsteller} tooltip={["Vul hier de organisatie in die het document initieel heeft gemaakt. Dit hoeft geen overheidsorganisatie te zijn, maar kan bijvoorbeeld ook een adviesbureau zijn.", "Als deze leeg is, dan is deze gelijk aan de openbaarmakende organisatie."]} />
          <SpacerAtom space={4} />
          <TextInputMolecule key={"creatiedatum"} id={"creatiedatum"} label={"Creatiedatum"} placeholder={"Kies creatiedatum"} value={creatiedatum} onChange={setCreatiedatum} tooltip={"Vul hier de datum in waarop het document initieel is gemaakt. Deze datum kan op de dag nauwkeurig zijn, maar ook een maand of jaar."} />
          <SpacerAtom space={4} />

          <LabelMolecule label={"Geldigheid"} tooltip={["Vul hier de periode in waarbinnen het document geldig is.", "De geldigheid kan gebruikt worden om aan te geven dat een document niet meer geldig is, bijvoorbeeld omdat het door een ander document is vervangen. Het oude document hoeft dan niet worden verwijderd, maar het is wel duidelijk dat het niet meer geldig is."]} />
          <ContainerAtom type="row">
            <DateInputMolecule key={"begindatum"} id={"begindatum"} placeholder={"Kies begindatum"} value={geldigheidBegindatum ? formatDate(stringToDate(geldigheidBegindatum.toString())) : undefined} onChange={(value) => { setGeldigheidBegindatum(stringToDate(value)) }} />
            <DateInputMolecule key={"einddatum"} id={"einddatum"} placeholder={"Kies einddatum"} value={geldigheidEinddatum ? formatDate(stringToDate(geldigheidEinddatum.toString())) : undefined} onChange={(value) => { setGeldigheidEinddatum(stringToDate(value)) }} />
          </ContainerAtom>
          <SpacerAtom space={4} />
          <TextInputMolecule key={"aggregatiekenmerk"} id={"aggregatiekenmerk"} label={"Aggregatiekenmerk"} placeholder={"Voer aggregatiekenmerk in"} value={aggregatiekenmerk} onChange={setAggregatiekenmerk} tooltip={"Vul hier de door aanleverende organisatie aangedragen tekst in, die een aantal documenten met elkaar gemeen kunnen hebben."} />
        </FoldableMolecule>

        <SpacerAtom space={4} />
        <FileInputMolecule inputRef={(r: any) => requiredRefs.push(r)} key={"file"} selectedFileName={document?.name ?? props.data[0].meta.document.titelcollectie.officieleTitel} onSelectFile={setDocument} required validations={fileInputValidations}></FileInputMolecule>
      </FormMolecule>
      {
        fetching && <SpinnerPopupMolecule />
      }
    </>
  );
}
