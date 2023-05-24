import { DesktopHiddenAtom, LabelAtom } from '../../../ui/atoms';
import { CenteredLayout } from '../../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms/Hidden',
    component: DesktopHiddenAtom,
    args: {
        children: 'Hidden bij desktop'
    },
} as ComponentMeta<typeof DesktopHiddenAtom>

export const DesktopHidden: ComponentStory<typeof DesktopHiddenAtom> = (args: any) => <CenteredLayout>
    <LabelAtom type='form'>De inhoud hieronder word verborgen op desktops</LabelAtom>
    <DesktopHiddenAtom>
        <LabelAtom type='default'>Desktop Hidden</LabelAtom>
    </DesktopHiddenAtom>
</CenteredLayout>