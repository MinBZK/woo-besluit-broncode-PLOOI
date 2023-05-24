import { SelectMultiMolecule } from "../../ui/molecules";
import { CenteredLayout } from "../../ui/layouts";
import { ComponentStory, ComponentMeta } from "@storybook/react";
import { ContainerAtom } from "../../ui/atoms";

export default {
  title: "KOOP-React/Molecules",
  component: SelectMultiMolecule,
  args: {
    id: "select",
    label: "Select options",
    placeholder: "Kies een of meerdere documentsoorten",
    tooltip: "Select options",
    required: true,
    disabled: false,
    categories: [
      {
        title: "Parlementaire documenten",

        options: [
          {
            id: "Moties",
            label: "Moties",
            checked: false,
            disabled: true,
          },
          {
            id: "Kamervragen",
            label: "Kamervragen",
            checked: false,
          },
          {
            id: "Agenda's",
            label: "Agenda's",
            checked: true,
            bold: true,
          },
        ],
      },
      {
        title: "Officiële bekendmakingen",
        options: [
          {
            id: "Staatsblad",
            label: "Staatsblad",
            checked: true,
          },
          {
            id: "Gemeenteblad",
            label: "Gemeenteblad",
            checked: false,
          },
          {
            id: "Waterschapsblad",
            label: "Waterschapsblad",
            checked: true,
          },
        ],
        sublist: [
          {
            title: "SubMenu Title",
            options: [
              {
                id: "Moties3",
                label: "Moties3",
                checked: true,
                disabled: true,
                bold: false,
                onClick: () => {},
              }
            ],
            sublist:[],
          },
        ],
      },
    ],
  },
} as ComponentMeta<typeof SelectMultiMolecule>;

export const SelectMulti: ComponentStory<typeof SelectMultiMolecule> = (
  args: any
) => (
  <CenteredLayout>
    <ContainerAtom>
      <SelectMultiMolecule {...args} />
    </ContainerAtom>
  </CenteredLayout>
);
