import { Identifier } from "../models";
import { ICheckbox } from "../ui/interfaces/Checkbox";
import { CategoryList } from "../ui/molecules/select-multi";
import { StringSanitizer } from "../utils/StringSanitizer";
import { CheckboxFactory } from "./checkbox";
import { IdentifierWithChildren } from "./waardelijst";

export class DropdownInfoFactory {
    private cbFactory: CheckboxFactory;
    private sanitizer: StringSanitizer;

    constructor() {
        this.sanitizer = new StringSanitizer();
        this.cbFactory = new CheckboxFactory();
    }

    onWaardelijstItemClicked = (item: Identifier, selectedValues: Identifier[], setter: (values: Identifier[]) => void) => {
        if (selectedValues.some((v) => v.id === item.id)) {
            setter([...selectedValues.filter((v) => v.id !== item.id)]);
            return;
        }

        setter([...selectedValues, item]);
    };

    // Voor multi dimensionale arrays 
    generateCategoryListMultiArray = (index: number, item: IdentifierWithChildren, selectedValues: Identifier[], setter: (index: number, values: Identifier) => void): CategoryList => {
        let list: CategoryList = ({
            title: this.sanitizer.capitalizeFirstLetter(item.label!),
            
            options: !item.children 
                ? [] 
                : item.children.map(child => this.cbFactory.create(
                this.sanitizer.sanitizeWaardelijstItem(child),
                selectedValues.some((v) => v.id === child.id),
                () => setter(index, child),
                false,
                false
            )),

            sublist: item.children?.filter(f => f.children).map(t => this.generateCategoryListMultiArray(index, t, selectedValues, setter))
        });

        return list;
    }

    // Voor niet-multi dimensionale arrays 
    generateCategoryListMultiSelect = (item: IdentifierWithChildren, selectedValues: Identifier[], setter: (values: Identifier[]) => void): CategoryList => {
        let list: CategoryList = {
            title: this.sanitizer.capitalizeFirstLetter(item!.label!),
            options: !item.children ? [] : item.children.filter(ds => !ds.children).map((ds) => this.cbFactory.create(
                this.sanitizer.sanitizeWaardelijstItem(ds),
                selectedValues.some((v) => v.id === ds.id),
                () => this.onWaardelijstItemClicked(ds, selectedValues, setter),
                false,
                false
            )
            ),
            sublist: item.children?.filter((f) => f.children).map((t) => this.generateCategoryListMultiSelect(t, selectedValues, setter)),
        };

        return list;
    };

    generateCheckboxListSingleSelect = (item: Identifier[], selectedValue: Identifier|undefined, setter: (values: Identifier) => void, defaultValue: Identifier): ICheckbox[] => {
        let list: ICheckbox[] = item.map((option) => this.cbFactory.create(
            option,
            selectedValue?.id === option?.id,
            () => setter(selectedValue?.id === option.id ? defaultValue : option),
            false,
            false
        )
        );

        return list;
    };
}