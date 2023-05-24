import { DefaultLayout } from "../../ui/layouts";
import { ComponentStory, ComponentMeta } from '@storybook/react';
import { OverheidHeaderOrganism } from "../../ui/organisms";

export interface Args {
    type?: 'primary'
}

export default {
    title: "KOOP-React/Organisms",
    component: OverheidHeaderOrganism
} as ComponentMeta<typeof OverheidHeaderOrganism>

export const OverheidHeader: ComponentStory<typeof OverheidHeaderOrganism> = (args: any) => <DefaultLayout header={
    <OverheidHeaderOrganism />
} body={<></>} footer={<></>} />