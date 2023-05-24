import { FlexLayout } from "../../layouts";

import {
  DocumentenLijstOrganism,
  OverheidHeaderOrganism,
  ToastOrganism,
} from "../../organisms";

export default function DocumentenLijstScreen() {
  return (
    <FlexLayout
      header={<>
        <ToastOrganism />
        <OverheidHeaderOrganism />
      </>
      }
      body={ <DocumentenLijstOrganism /> }
    />
  );
}
