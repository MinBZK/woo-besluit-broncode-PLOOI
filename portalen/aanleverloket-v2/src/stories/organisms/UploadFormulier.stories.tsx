import { UpdateFormulierOrganism } from "../../ui/organisms";
import { CenteredLayout } from "../../ui/layouts";
import { ComponentStory, ComponentMeta } from "@storybook/react";
import { Provider } from "react-redux";
import { setupStore } from "../../store";
import { ContainerAtom } from "../../ui/atoms";

export default {
  title: "KOOP-React/Organisms",
  component: UpdateFormulierOrganism,
  args: {
    data: [{
      meta: {
        document: {
          publisher: {
            id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034",
            label: "ministerie van Binnenlandse Zaken en Koninkrijksrelaties",
          },
          identifiers: ["test"],
          language: {
            id: "http://publications.europa.eu/resource/authority/language/NLD",
            label: "Nederlands",
          },
          titelcollectie: {
            officieleTitel: "title",
          },
          classificatiecollectie: {
            documentsoorten: [
              {
                id: "Soort1",
                label: "Soort1",
              },
            ],
            themas: [
              {
                id: "Thema1",
                label: "Thema1",
              },
            ],
          },
          documenthandelingen: [
            {
              soortHandeling: {
                id: "https://identifier.overheid.nl/tooi/def/thes/kern/c_dfcee535",
                label: "ontvangst",
              },
              atTime: new Date().toJSON(),
              wasAssociatedWith: {
                id: "https://identifier.overheid.nl/tooi/id/ministerie/mnre1034",
                label: "ministerie van Binnenlandse Zaken en Koninkrijksrelaties",
              },
            },
          ],
        },
      }
    },]
  },
} as ComponentMeta<typeof UpdateFormulierOrganism>;

export const UploadFormulier: ComponentStory<typeof UpdateFormulierOrganism> = (
  args: any
) => (
  <Provider store={setupStore()}>
    <CenteredLayout>
      <ContainerAtom>
        <UpdateFormulierOrganism {...args} />
      </ContainerAtom>
    </CenteredLayout>
  </Provider>
);
