# cl-mvps (Consumer Lending MVPs)

This is the main branch for the Consumer Lending MVPs (Minimum Viable Products) project. This branch, `main`, is intended to remain **blank** and **protected** at all times. It serves as the pristine starting point for all new MVP development efforts. A new `master` branch is created from main to merge the newly created mvp branch into master. Create a PR to merge the mvp branch into master.

## Purpose

The purpose of this repository is to facilitate the development and demonstration of MVPs within the Consumer Lending domain. By maintaining a clean and empty `main` branch, we ensure:

*   **Consistent Starting Point:** Every new MVP begins with a clean slate, preventing conflicts and ensuring a uniform foundation.
*   **Clear MVP Scope:** Each MVP is isolated within its own branch, promoting clarity and focus.
*   **Controlled Development:** Changes are made within feature branches and reviewed before potential integration, ensuring quality and stability.

## How to Initiate a New MVP

1.  **Branching:**
    *   Never work directly in the `main` branch.
    *   To start a new MVP, create a new branch from `main`.
    *   Use a descriptive branch name that clearly indicates the MVP's purpose and scope.
    * The branch name should be `<MVP-NAME>`.
    *   Example: `salsa`, `generative-appraisal`, `document-search`

2.  **MVP Description in the Branch:**
    *   Inside your new branch `<MVP-NAME>`, create a README.md that describes your MVP.
    *   In this README.md, provide details about:
        *   **MVP Name:** The name of your MVP.
        *   **MVP Goal:** The specific problem or opportunity this MVP addresses.
        *   **MVP Scope:** The key features and functionalities included in this MVP.
        *   **Technical Details:** Relevant architecture, technologies, and dependencies.
        *   **Setup Instructions:** Steps to build, deploy, and run the MVP.
        *   **Dependencies:** List of required tools and libraries.
        * **Links**: Provide links to relevant documents, test reports, etc.

3.  **Development:**
    *   Implement the MVP's functionalities within your branch.
    *   Commit and push your changes regularly.

4.  **Review and Approval:**
    *   Once the MVP is ready for review, create a pull request to merge your branch into `master`.
    *   The pull request description should contain a summary of the MVP and any relevant information.
    *   Ensure that the team reviews and approves the pull request before merging.
    * **DO NOT** merge this to main, as it is intended to be empty. Keep it on your branch or delete it when it is not needed.

5. **MVP Naming Convention**
   * Follow the bellowing convention for naming the MVP:
      * `<MVP-FUNCTIONALITY>-<TECHNOLOGY>`
   * Examples:
      * `document-summary-cloud-function`
      * `appraisal-gemini`
      * `loan-application-automation`

## Current MVPs

*   **See individual MVP branches for their respective README.md files.**

## Link to MVP Readme.md

*  **generative-appraisal MVP**: [generative-appraisal/README.md](generative-appraisal/README.md)

## Support

If you have any questions or need assistance, please contact the Consumer Lending team.