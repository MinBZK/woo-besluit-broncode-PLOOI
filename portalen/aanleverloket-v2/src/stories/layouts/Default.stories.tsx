import { DefaultLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';
import { HeadingH1Atom } from '../../ui/atoms';

export default {
    title: 'KOOP-React/Layouts',
    component: DefaultLayout
} as ComponentMeta<typeof DefaultLayout>

export const Default: ComponentStory<typeof DefaultLayout> = (args: any) => <DefaultLayout
    header={<HeadingH1Atom>Header Positie</HeadingH1Atom>}
    body={<HeadingH1Atom>Body Positie</HeadingH1Atom>}
    footer={<HeadingH1Atom>Footer Positie</HeadingH1Atom>}
/>