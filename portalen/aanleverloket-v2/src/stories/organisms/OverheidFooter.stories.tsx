import { DefaultLayout } from "../../ui/layouts";
import { ComponentStory, ComponentMeta } from '@storybook/react';
import { OverheidFooterOrganism } from "../../ui/organisms";

export interface Args {
    type?: 'primary'
}

export default {
    title: "KOOP-React/Organisms",
    component: OverheidFooterOrganism
} as ComponentMeta<typeof OverheidFooterOrganism>

export const OverheidFooter: ComponentStory<typeof OverheidFooterOrganism> = (args: any) => 
    <DefaultLayout header={<></>} body={<></>} footer={<OverheidFooterOrganism />} />