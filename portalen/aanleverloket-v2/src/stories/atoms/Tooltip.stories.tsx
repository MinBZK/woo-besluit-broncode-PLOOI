import { TooltipAtom } from '../../ui/atoms';
import { CenteredLayout } from '../../ui/layouts';
import { ComponentStory, ComponentMeta } from '@storybook/react';

export default {
    title: 'KOOP-React/Atoms',
    component: TooltipAtom,
    args: { 
        children: "Tooltip Atom",
        onMouseEnter: () => {},
        onMouseLeave: () => {}
    },
} as ComponentMeta<typeof TooltipAtom>

export const Tooltip: ComponentStory<typeof TooltipAtom> = (args: any) => <CenteredLayout>
    <TooltipAtom {...args} />
</CenteredLayout>