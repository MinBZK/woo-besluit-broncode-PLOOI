import { MobileHiddenAtom, LabelAtom } from '../../../ui/atoms';
import { CenteredLayout } from '../../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms/Hidden',
    component: MobileHiddenAtom,
    args: {
        children: 'Hidden bij Mobile'
    },
} as ComponentMeta<typeof MobileHiddenAtom>

export const MobileHidden: ComponentStory<typeof MobileHiddenAtom> = (args: any) => <CenteredLayout>
    <LabelAtom type='form'>De inhoud hieronder word verborgen op mobiele devices</LabelAtom>
    <MobileHiddenAtom>
        <LabelAtom type='default'>Mobile Hidden</LabelAtom>
    </MobileHiddenAtom>
</CenteredLayout>