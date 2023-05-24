import { DefaultLayout } from "../../layouts";

import {
  LoginFormOrganism,
  OverheidFooterOrganism,
  OverheidHeaderOrganism,
  ToastOrganism,
} from "../../organisms";

export default function LoginScreen() {
  return (
    <DefaultLayout
      header={<>
        <ToastOrganism />
        <OverheidHeaderOrganism />
      </>
      }
      body={<LoginFormOrganism />}
    />
  );
}
